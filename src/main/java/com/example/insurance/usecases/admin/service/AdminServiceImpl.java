package com.example.insurance.usecases.admin.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.insurance.domain.claim.model.Claim;
import com.example.insurance.domain.claim.model.IncidentDetails;
import com.example.insurance.domain.claim.repository.ClaimRepository;
import com.example.insurance.domain.claim.service.ClaimMapper;
import com.example.insurance.domain.claim.service.ClaimService;
import com.example.insurance.domain.customer.model.Customer;
import com.example.insurance.domain.customer.repository.CustomerRepository;
import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.customerPolicy.model.PaymentFrequency;
import com.example.insurance.domain.customerPolicy.repository.CustomerPolicyRepository;
import com.example.insurance.domain.customerPolicy.service.PolicyMapper;
import com.example.insurance.domain.paymentSchedule.model.PaymentSchedule;
import com.example.insurance.domain.paymentSchedule.model.PaymentStatus;
import com.example.insurance.domain.paymentSchedule.repository.PaymentScheduleRepository;
import com.example.insurance.domain.paymentSchedule.service.PaymentScheduleService;
import com.example.insurance.domain.policyBeneficiary.model.PolicyBeneficiary;
import com.example.insurance.infrastructure.web.claim.ClaimResponseDTO;
import com.example.insurance.infrastructure.web.custommerPolicy.InsurancePolicyDto;
import com.example.insurance.shared.enummuration.ClaimStatus;
import com.example.insurance.shared.enummuration.PolicyStatus;
import com.example.insurance.shared.kernel.embeddables.Address;
import com.example.insurance.shared.kernel.embeddables.ThirdPartyDetails;
import com.example.insurance.usecases.admin.controller.AdminAllPaymentsDto;
import com.example.insurance.usecases.admin.controller.AdminClaimUpdateRequest;
import com.example.insurance.usecases.admin.controller.AdminCustommersDto;
import com.example.insurance.usecases.admin.controller.AdminPolicyRequestDto;
import com.example.insurance.usecases.admin.controller.UpdateCustomerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final CustomerPolicyRepository customerPolicyRepository;
    private final PaymentScheduleService paymentScheduleService;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final ClaimRepository claimRepository;
    private final ClaimService claimService;
    private final CustomerRepository customerRepository;

    @Override
    public List<InsurancePolicyDto> getAllPolicies() {

        List<CustomerPolicy> policies = customerPolicyRepository.findAll();

        return policies.stream().map(policy -> PolicyMapper.toDto(policy)).toList();
    }

    @Override
    public List<ClaimResponseDTO> getAllClaims(ClaimSortRequest sortRequest) {

        // Determine sort direction
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortRequest.getSortDirection()) ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        // Determine sort field (map from request field to entity field)
        String sortField = AdminMapper.mapSortField(sortRequest.getSortBy());

        // Create Sort Object
        Sort sort = Sort.by(direction, sortField);

        // Get sorted claim from repository
        // List<Claim> getAllClaims = claimRepository.findAll();
        List<Claim> allCaims = claimRepository.findAllWithSorting(sort);

        return allCaims.stream().map((claim) -> ClaimMapper.mapToDto(claim)).toList();
    }

    @Override
    public ClaimResponseDTO getClaimDetails(Long claimId) {

        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim Not Found!"));
        return ClaimMapper.mapToAdminDto(claim);
    }

    @Override
    @Transactional
    public void updatePolicy(AdminPolicyRequestDto dto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminUser = authentication.getName();
        CustomerPolicy policy = customerPolicyRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Policy not found!"));

        PaymentFrequency newPaymentFrequency = PaymentFrequency.valueOf(dto.getPaymentFrequency());
        PaymentFrequency oldPaymentFrequency = policy.getPaymentFrequency();
        boolean premiumChanged = false;

        // Save old status for comparison
        PolicyStatus oldStatus = policy.getStatus();
        PolicyStatus newStatus = PolicyStatus.valueOf(dto.getStatus());

        // Validate status transition
        validateStatusTransition(oldStatus, newStatus);

        // Get effective date from DTO
        LocalDate newEffectiveDate = dto.getValidityPeriod().getEffectiveDate();
        LocalDate today = LocalDate.now();

        // CRITICAL: Validate that policy cannot be ACTIVE with future effective date
        if (newStatus == PolicyStatus.ACTIVE && newEffectiveDate.isAfter(today)) {
            throw new IllegalArgumentException(
                    "Cannot activate policy with future effective date: " + newEffectiveDate +
                            ". To activate, set effective date to today (" + today + ") or earlier.");
        }

        // Also validate that ACTIVE policy cannot have its effective date changed to
        // future
        if (oldStatus == PolicyStatus.ACTIVE && newEffectiveDate.isAfter(today)) {
            throw new IllegalArgumentException(
                    "Cannot change effective date of active policy to future date: " + newEffectiveDate);
        }

        // For CANCELLED status, require cancellation reason
        if (newStatus == PolicyStatus.CANCELLED && oldStatus != PolicyStatus.CANCELLED) {
            if (dto.getCancellationReason() == null || dto.getCancellationReason().trim().isEmpty()) {
                throw new IllegalArgumentException("Cancellation reason is required");
            }
        }

        // Update basic fields
        policy.setStatus(newStatus);
        policy.setPaymentFrequency(newPaymentFrequency);
        policy.getCoveragePeriod().setEffectiveDate(dto.getValidityPeriod().getEffectiveDate());
        policy.getCoveragePeriod().setExpirationDate(dto.getValidityPeriod().getExpirationDate());

        // Set status change notes if provided
        if (dto.getStatusChangeNotes() != null) {
            policy.setStatusChangeNotes(dto.getStatusChangeNotes());
        }

        // Set updated audit fields
        policy.setUpdatedBy(adminUser);
        policy.setUpdatedAt(Instant.now());

        // Handle status-specific logic
        if (newStatus != oldStatus) {
            handleStatusChange(policy, dto, oldStatus, newStatus);
        }

        // Check if Premium changed
        BigDecimal newPremium = dto.getPremium();
        BigDecimal oldPremium = policy.getPremium().getAmount();

        if (newPremium != null && !newPremium.equals(oldPremium)) {
            policy.getPremium().setAmount(newPremium);
            premiumChanged = true;
        }

        // Handle beneficiaries
        if (dto.getBeneficiaries() != null) {
            policy.getBeneficiaries().clear();
            List<PolicyBeneficiary> newBeneficiaries = dto.getBeneficiaries().stream()
                    .map(PolicyMapper::mapToBeneficiaryEntity)
                    .peek(beneficiary -> beneficiary.setCustomerPolicy(policy))
                    .toList();
            policy.getBeneficiaries().addAll(newBeneficiaries);
        }

        CustomerPolicy savedPolicy = customerPolicyRepository.save(policy);

        // Only regenerate payment schedules if needed AND policy is not cancelled
        boolean paymentFrequencyChanged = !oldPaymentFrequency.equals(newPaymentFrequency);
        if ((premiumChanged || paymentFrequencyChanged) && newStatus != PolicyStatus.CANCELLED) {
            paymentScheduleService.regeneratePaymentSchedule(savedPolicy, newPaymentFrequency);
        }
    }

    @Override
    public void updateClaim(AdminClaimUpdateRequest dto) {

        Claim claim = claimService.findByClaimNumber(dto.getClaimId());

        // Validate status transition if status is being changed
        if (dto.getStatus() != null && !claim.getStatus().equals(dto.getStatus())) {
            if (!claim.canTransitionTo(dto.getStatus())) {
                throw new IllegalStateException(
                        String.format("Cannot change claim status from %s to %s",
                                claim.getStatus(), dto.getStatus()));
            }
        }

        claim.setStatus(dto.getStatus());
        claim.setAmount(dto.getAmount());

        // Use existing IncidentDetails
        IncidentDetails incidentDetails = claim.getIncidentDetails();

        // If incidentDetails is null(Shouldn't happen for existing claims), then create
        // one
        if (incidentDetails == null) {
            incidentDetails = new IncidentDetails();
            claim.setIncidentDetails(incidentDetails);
        }

        incidentDetails.setDescription(dto.getIncidentDetails().getDescription());
        incidentDetails.setPoliceReportNumber(dto.getIncidentDetails().getPoliceReportNumber());

        Address address = claim.getIncidentDetails().getLocation();
        address.setStreet(dto.getIncidentDetails().getLocation().getStreet());
        address.setCity(dto.getIncidentDetails().getLocation().getCity());
        address.setPostalCode(dto.getIncidentDetails().getLocation().getPostalCode());
        address.setCountry(dto.getIncidentDetails().getLocation().getCountry());

        if (dto.getIncidentDetails().getThirdPartyDetails() != null) {

            ThirdPartyDetails thirdPartyDetails = new ThirdPartyDetails();
            thirdPartyDetails.setName(dto.getIncidentDetails().getThirdPartyDetails().getName());
            thirdPartyDetails.setContactInfo(dto.getIncidentDetails().getThirdPartyDetails().getContactInfo());
            thirdPartyDetails.setInsuranceInfo(dto.getIncidentDetails().getThirdPartyDetails().getInsuranceInfo());
            thirdPartyDetails.setPolicyNumber(dto.getPolicyNumber());

            incidentDetails.setThirdPartyDetails(thirdPartyDetails);
        }
        claimRepository.save(claim);

    }

    @Override
    public PaymentSummaryDto getAllPayments() {
        LocalDate today = LocalDate.now();

        // 1. all schedules in one query (eager-load policy & customer)
        List<PaymentSchedule> all = paymentScheduleRepository.findAllWithPolicyAndCustomer();

        // 2. split into three buckets
        List<AdminAllPaymentsDto> coming = new ArrayList<>();
        List<AdminAllPaymentsDto> overdue = new ArrayList<>();
        List<AdminAllPaymentsDto> paid = new ArrayList<>();
        List<AdminAllPaymentsDto> paused = new ArrayList<>();
        List<AdminAllPaymentsDto> cancelled = new ArrayList<>();

        for (PaymentSchedule ps : all) {
            AdminAllPaymentsDto dto = buildDto(ps);
            if (ps.getStatus() == PaymentStatus.PAID) {
                paid.add(dto);
            } else if (ps.getDueDate().isBefore(today)) {
                overdue.add(dto);
            } else if (ps.getStatus() == PaymentStatus.CANCELLED) {
                cancelled.add(dto);

            } else if (ps.getStatus() == PaymentStatus.PAUSED) {
                paused.add(dto);
            } else {
                coming.add(dto);

            }
        }

        return new PaymentSummaryDto(coming, overdue, paid, paused, cancelled);

    }

    // Get all customers
    @Override
    public List<AdminCustommersDto> getAllCustomers() {
        List<Customer> findAllCustomers = customerRepository.findAll();

        return findAllCustomers.stream().map(AdminMapper::toAdminCustomerDto).toList();
    }

    // Get single customer by user id
    public AdminCustommersDto getCustomerByUserId(String userId) {

        Customer customer = getByUserId(userId);

        return AdminMapper.toAdminCustomerDto(customer);
    }

    @Override
    public AdminCustommersDto updateCustomer(UpdateCustomerDto dto) {

        Customer customer = getByUserId(dto.getCustomerId());

        customer.getName().setFirstName(dto.getCustomerFirstname());
        customer.getName().setLastName(dto.getCustomerLastname());
        customer.setEmail(dto.getEmail());
        customer.setDateOfBirth(dto.getCustomerDateOfBirth());
        customer.getContactInfo().setPhone(dto.getCustomerPhone());
        if (dto.getCustomerAlternativePhone() != null) {
            customer.getContactInfo().setAlternatePhone(dto.getCustomerAlternativePhone());
        }
        customer.getContactInfo().getPrimaryAddress().setStreet(dto.getCustomerPrimaryAddressStreet());
        customer.getContactInfo().getPrimaryAddress().setCity(dto.getCustomerPrimaryAddressCity());
        customer.getContactInfo().getPrimaryAddress().setPostalCode(dto.getCustomerPrimaryAddressPostalCode());
        customer.getContactInfo().getPrimaryAddress().setCountry(dto.getCustomerPrimaryAddressCountry());
        customer.getContactInfo().getBillingAddress().setStreet(dto.getCustomerBillingAddressStreet());
        customer.getContactInfo().getBillingAddress().setCity(dto.getCustomerBillingAddressCity());
        customer.getContactInfo().getBillingAddress().setPostalCode(dto.getCustomerBillingAddressPostalCode());
        customer.getContactInfo().getBillingAddress().setCountry(dto.getCustomerBillingAddressCountry());

        Customer savedCustomer = customerRepository.save(customer);

        return AdminMapper.toAdminCustomerDto(savedCustomer);

    }

    /* ------- helper ------- */
    private AdminAllPaymentsDto buildDto(PaymentSchedule ps) {
        CustomerPolicy policy = ps.getPolicy();
        Customer customer = policy.getPolicyHolder();
        return new AdminAllPaymentsDto(
                ps.getId(),
                customer.getName().getFirstName() + " " + customer.getName().getLastName(),
                policy.getPolicyNumber(),
                ps.getDueAmount(),
                ps.getDueAmount().getCurrency(),
                ps.getDueDate(),
                ps.getPaidDate(),
                ps.getStatus());
    }

    private Customer getByUserId(String userId) {
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer Not found1"));

        return customer;
    }

    @Override
    @Transactional
    public void approveClaim(Long claimId, ApproveClaimRequest request) {
        // Gete Current Admin
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String adminUserName = authentication.getName();

        // 2. Basic validation
        if (request.getApprovedAmount() == null || request.getApprovedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Approved amount must be greater than zero");
        }

        // 3. Find claim
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found with ID: " + claimId));

        // 4. Validate state transition
        if (!claim.canTransitionTo(ClaimStatus.APPROVED)) {
            throw new IllegalStateException(
                    "Claim cannot be approved. Current status: " + claim.getStatus() +
                            ". Valid transitions from " + claim.getStatus() + ": " +
                            getClaimValidTransitions(claim.getStatus()));

        }

        // 5. Validate approved amount doesn't exceed claimed amount
        if (claim.getAmount() != null && request.getApprovedAmount().compareTo(claim.getAmount()) > 0) {
            throw new IllegalArgumentException(
                    "Approved amount (" + request.getApprovedAmount() +
                            ") cannot exceed claimed amount (" + claim.getAmount() + ")");
        }

        // 6. Process approval
        claim.approve(adminUserName, request.getApprovedAmount());

        // Add approval notes if provided
        if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
            claim.setApprovalNotes(request.getNotes());
        }

        // 7. Save claim
        claim = claimRepository.save(claim);
    };

    @Override
    @Transactional
    public void rejectClaim(Long claimId, RejectClaimRequest request) {
        // Get current admin username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminUsername = authentication.getName();

        // Find claim
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found with ID: " + claimId));

        // Validate claim can be rejected
        if (!claim.canTransitionTo(ClaimStatus.REJECTED)) {
            throw new IllegalStateException(
                    "Claim cannot be rejected. Current status: " + claim.getStatus() +
                            ". Valid transitions from " + claim.getStatus() + ": " +
                            getClaimValidTransitions(claim.getStatus()));
        }

        // Validate rejection reason
        if (request.getRejectionReason() == null ||
                request.getRejectionReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Rejection reason is required");
        }

        // Reject the claim
        claim.reject(adminUsername, request.getRejectionReason());

        // Store rejection notes if provided
        if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {

        }
        claimRepository.save(claim);
        log.info("Claim {} rejected by admin {}", claim.getClaimNumber(), adminUsername);
    }

    @Override
    @Transactional
    public void markClaimAsPaid(Long claimId, MarkAsPaidRequest request) {

        // Get current Admin
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminUser = authentication.getName();

        // Find Claim
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found with Id: " + claimId));

        // Validate claim can be paid
        if (!claim.canBePaid()) {
            throw new IllegalStateException("Claim cannot be paid. Status: " + claim.getStatus() + ", Payment Status: "
                    + claim.getPaymentStatus() + ", Approved Amount: " + claim.getApprovedAmount());
        }

        // mark as Paid
        claim.markAsPaid(adminUser);

        claimRepository.save(claim);
        log.info("Claim {} marked as paid by admin {}", claim.getClaimNumber(), adminUser);

    }

    private String getClaimValidTransitions(ClaimStatus currentStatus) {
        switch (currentStatus) {
            case PENDING:
                return "UNDER_REVIEW, APPROVED, REJECTED";
            case UNDER_REVIEW:
                return "APPROVED, REJECTED";
            case APPROVED:
                return "PAID, REJECTED";
            default:
                return "No valid transitions (terminal state)";
        }

    }

    private void validateStatusTransition(PolicyStatus oldStatus, PolicyStatus newStatus) {
        // Define valid status transitions
        Map<PolicyStatus, List<PolicyStatus>> validTransitions = Map.of(
                PolicyStatus.ACTIVE, List.of(PolicyStatus.INACTIVE, PolicyStatus.CANCELLED, PolicyStatus.EXPIRED),
                PolicyStatus.INACTIVE, List.of(PolicyStatus.ACTIVE, PolicyStatus.CANCELLED, PolicyStatus.EXPIRED),
                PolicyStatus.PENDING, List.of(PolicyStatus.ACTIVE, PolicyStatus.CANCELLED, PolicyStatus.EXPIRED),
                PolicyStatus.EXPIRED, List.of(PolicyStatus.CANCELLED),
                PolicyStatus.CANCELLED, List.of() // Once cancelled, cannot change to other statuses
        );

        if (oldStatus != newStatus) {
            List<PolicyStatus> allowedTransitions = validTransitions.get(oldStatus);
            if (!allowedTransitions.contains(newStatus)) {
                throw new IllegalStateException(
                        String.format("Invalid status transition from %s to %s", oldStatus, newStatus));
            }
        }

    }

    /**
     * Handle status change side effects
     */
    private void handleStatusChange(CustomerPolicy policy, AdminPolicyRequestDto dto,
            PolicyStatus oldStatus, PolicyStatus newStatus) {

        if (newStatus == PolicyStatus.CANCELLED && oldStatus != PolicyStatus.CANCELLED) {
            handlePolicyCancellation(policy, dto);
            // } else if (oldStatus == PolicyStatus.CANCELLED && newStatus !=
            // PolicyStatus.CANCELLED) {
            // handlePolicyReactivation(policy);
        } else if (newStatus == PolicyStatus.EXPIRED) {
            handlePolicyExpiration(policy);
        } else if (newStatus == PolicyStatus.PENDING) {
            handlePolicyPending(policy);
        } else if (oldStatus == PolicyStatus.PENDING && newStatus == PolicyStatus.ACTIVE) {
            handlePolicyActivationFromPending(policy);
        } else if (newStatus == PolicyStatus.INACTIVE && oldStatus == PolicyStatus.ACTIVE) {
            handlePolicyDeactivation(policy);
        } else if (oldStatus == PolicyStatus.INACTIVE && newStatus == PolicyStatus.ACTIVE) {
            handlePolicyReactivationFromInactive(policy);
        }
    }

    /**
     * Handle policy pending
     */
    private void handlePolicyPending(CustomerPolicy policy) {
        // Pause all future payment schedules
        if (policy.getPaymentSchedules() != null) {
            policy.getPaymentSchedules().forEach(schedule -> {
                if (schedule.getStatus() == PaymentStatus.PENDING) {
                    schedule.setStatus(PaymentStatus.PAUSED);
                }
            });
        }

        pauseClaimsForPolicy(policy);
        log.info("Policy {} set to PENDING - payment schedules paused", policy.getPolicyNumber());
    }

    /**
     * Handle policy handlePolicyActivationFromPending
     */
    private void handlePolicyActivationFromPending(CustomerPolicy policy) {
        // Reactivate paused payment schedules
        LocalDate today = LocalDate.now();
        if (policy.getPaymentSchedules() != null) {
            policy.getPaymentSchedules().forEach(schedule -> {
                if (schedule.getStatus() == PaymentStatus.PAUSED &&
                        !schedule.getDueDate().isBefore(today)) {
                    schedule.setStatus(PaymentStatus.PENDING);
                }
            });
        }

        // Reactivate paused claims associated with this policy
        reactivateClaimsForPolicy(policy);
        log.info("Policy {} activated from PENDING - payment schedules reactivated", policy.getPolicyNumber());
    }

    /**
     * Handle policy cancellation
     */
    private void handlePolicyCancellation(CustomerPolicy policy, AdminPolicyRequestDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminUser = authentication.getName();

        // Set cancellation details on policy
        policy.setCancellationReason(dto.getCancellationReason());
        policy.setCancellationDate(LocalDate.now());
        policy.setCancelledBy(adminUser);

        // Cancel payment schedules
        cancelPaymentSchedules(policy, adminUser);

        // Cancel any pending claims
        cancelPendingClaims(policy, adminUser);
    }

    /**
     * Handle policy deactivation (ACTIVE → INACTIVE)
     */
    private void handlePolicyDeactivation(CustomerPolicy policy) {
        // Pause all future payment schedules
        if (policy.getPaymentSchedules() != null) {
            policy.getPaymentSchedules().forEach(schedule -> {
                if (schedule.getStatus() == PaymentStatus.PENDING ||
                        schedule.getStatus() == PaymentStatus.OVERDUE) {
                    schedule.setStatus(PaymentStatus.PAUSED);
                }
            });
        }

        // Pause all active/pending claims associated with this policy
        pauseClaimsForPolicy(policy);
        log.info("Policy {} deactivated to INACTIVE - payment schedules paused", policy.getPolicyNumber());
    }

    /**
     * Handle policy reactivation from INACTIVE to ACTIVE
     */
    private void handlePolicyReactivationFromInactive(CustomerPolicy policy) {
        // Reactivate paused payment schedules
        LocalDate today = LocalDate.now();
        if (policy.getPaymentSchedules() != null) {
            policy.getPaymentSchedules().forEach(schedule -> {
                if (schedule.getStatus() == PaymentStatus.PAUSED) {
                    // Check if this schedule is still relevant
                    if (!schedule.getDueDate().isBefore(today)) {
                        schedule.setStatus(PaymentStatus.PENDING);
                    } else {
                        // If due date has passed, mark as OVERDUE
                        schedule.setStatus(PaymentStatus.OVERDUE);
                    }
                }
            });
        }

        // Reactivate paused claims associated with this policy
        reactivateClaimsForPolicy(policy);
        log.info("Policy {} reactivated from INACTIVE - payment schedules restored", policy.getPolicyNumber());
    }

    /**
     * Handle policy expiration
     */
    private void handlePolicyExpiration(CustomerPolicy policy) {
        LocalDate today = LocalDate.now();
        if (policy.getPaymentSchedules() != null) {
            policy.getPaymentSchedules().forEach(schedule -> {
                if (!schedule.getDueDate().isBefore(today) && // Includes today and future dates
                        (schedule.getStatus() == PaymentStatus.PENDING ||
                                schedule.getStatus() == PaymentStatus.PAUSED)) {
                    schedule.setStatus(PaymentStatus.CANCELLED);
                    schedule.setCancellationDate(today);
                    schedule.setCancelledBy("System - Policy Expired");
                }
            });
        }

        // Pause all active/pending claims associated with this policy
        pauseClaimsForPolicy(policy);
        log.info("Policy {} expired - future payment schedules cancelled", policy.getPolicyNumber());
    }

    private void cancelPaymentSchedules(CustomerPolicy policy, String cancelledBy) {
        if (policy.getPaymentSchedules() != null) {
            List<PaymentSchedule> schedulesToCancel = new ArrayList<>();

            policy.getPaymentSchedules().forEach(schedule -> {
                // Cancel all schedules except PAID ones
                if (schedule.getStatus() != PaymentStatus.PAID) {
                    schedule.cancel(cancelledBy);
                    schedulesToCancel.add(schedule);

                }
            });

            // Save the updated payment schedules
            if (!schedulesToCancel.isEmpty()) {
                paymentScheduleRepository.saveAll(schedulesToCancel);
            }
        }
    }

    /**
     * Pause claims when policy is deactivated
     */
    private void pauseClaimsForPolicy(CustomerPolicy policy) {
        // Get all non-terminal claims for this policy
        List<Claim> claimsToPause = claimRepository.findByPolicyNumberAndStatusIn(
                policy.getPolicyNumber(),
                List.of(ClaimStatus.PENDING, ClaimStatus.UNDER_REVIEW));

        if (!claimsToPause.isEmpty()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminUser = authentication.getName();

            claimsToPause.forEach(claim -> {
                claim.setStatus(ClaimStatus.PAUSED);
                // claim.setStatusChangeNotes("Claim paused due to policy deactivation");
                claim.setUpdatedBy(adminUser);
                claim.setUpdatedAt(Instant.now());
            });

            claimRepository.saveAll(claimsToPause);
            log.info("Paused {} claims for policy {}", claimsToPause.size(), policy.getPolicyNumber());
        }
    }

    /**
     * Reactivate claims when policy is reactivated
     */
    private void reactivateClaimsForPolicy(CustomerPolicy policy) {
        // Get all paused claims for this policy
        List<Claim> claimsToReactivate = claimRepository.findByPolicyNumberAndStatus(
                policy.getPolicyNumber(),
                ClaimStatus.PAUSED);

        if (!claimsToReactivate.isEmpty()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminUser = authentication.getName();

            claimsToReactivate.forEach(claim -> {

                // You might want to set claims back to their original state before pause
                // or set them to a default state like PENDING
                claim.setStatus(ClaimStatus.PENDING);
                // claim.setStatusChangeNotes("Claim reactivated due to policy reactivation");
                claim.setUpdatedBy(adminUser);
                claim.setUpdatedAt(Instant.now());
            });

            claimRepository.saveAll(claimsToReactivate);
            log.info("Reactivated {} claims for policy {}", claimsToReactivate.size(), policy.getPolicyNumber());
        }
    }

    private void cancelPendingClaims(CustomerPolicy policy, String adminusername) {
        List<Claim> pendingClaims = claimRepository.findByPolicyNumberAndStatusIn(policy.getPolicyNumber(),
                List.of(ClaimStatus.PENDING, ClaimStatus.UNDER_REVIEW, ClaimStatus.EXPIRED));

        pendingClaims.forEach(claim -> {
            claim.setStatus(ClaimStatus.CANCELLED);
            claim.setCancellationReason("Policy canncelled by admin");
            claim.setCancelledBy(adminusername);
            claim.setCancellationDate(LocalDate.now());
        });
        claimRepository.saveAll(pendingClaims);
    }
};
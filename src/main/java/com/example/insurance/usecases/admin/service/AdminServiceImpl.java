package com.example.insurance.usecases.admin.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.insurance.common.enummuration.ClaimStatus;
import com.example.insurance.common.enummuration.PolicyStatus;
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
import com.example.insurance.embeddable.ThirdPartyDetails;
import com.example.insurance.infrastructure.web.claim.ClaimResponseDTO;
import com.example.insurance.infrastructure.web.custommerPolicy.InsurancePolicyDto;
import com.example.insurance.shared.kernel.embeddables.Address;
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
    public List<ClaimResponseDTO> getAllClaims() {
        List<Claim> getAllClaims = claimRepository.findAll();

        return getAllClaims.stream().map((claim) -> ClaimMapper.mapToDto(claim)).toList();
    }

    @Override
    public void updatePolicy(AdminPolicyRequestDto dto) {

        CustomerPolicy policy = customerPolicyRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Policy not found!"));

        if (policy != null) {
            PaymentFrequency newPaymentFrequency = PaymentFrequency.valueOf(dto.getPaymentFrequency());
            PaymentFrequency oldPaymentFrequency = policy.getPaymentFrequency();
            boolean premiumChanged = false;

            // Update basic fields
            policy.setStatus(PolicyStatus.valueOf(dto.getStatus()));
            // policy.getPremium().setAmount(dto.getPremium());
            policy.setPaymentFrequency(newPaymentFrequency);
            policy.getCoveragePeriod().setEffectiveDate(dto.getValidityPeriod().getEffectiveDate());
            policy.getCoveragePeriod().setExpirationDate(dto.getValidityPeriod().getExpirationDate());

            // Check if Premium changed
            BigDecimal newPremium = dto.getPremium();
            BigDecimal odlPremium = policy.getPremium().getAmount();

            if (newPremium != null && !newPremium.equals(odlPremium)) {
                policy.getPremium().setAmount(newPremium);
                premiumChanged = true;
            }

            // Handle beneficiaries
            if (dto.getBeneficiaries() != null) {
                // ¨Clear existing beneficiries
                policy.getBeneficiaries().clear();

                // Map new DTOs to entities and add them
                List<PolicyBeneficiary> newBeneficiaries = dto.getBeneficiaries().stream()
                        .map(PolicyMapper::mapToBeneficiaryEntity)
                        .peek(beneficiary -> beneficiary.setCustomerPolicy(policy))
                        .toList();

                policy.getBeneficiaries().addAll(newBeneficiaries);
            }

            boolean paymentFrequencyChanged = !oldPaymentFrequency.equals(newPaymentFrequency);

            CustomerPolicy savedPolicy = customerPolicyRepository.save(policy);

            // Regenerate the payment Scheules using the saved policy
            if (premiumChanged || paymentFrequencyChanged) {

                paymentScheduleService.regeneratePaymentSchedule(savedPolicy, newPaymentFrequency);
            }

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

        for (PaymentSchedule ps : all) {
            AdminAllPaymentsDto dto = buildDto(ps);
            if (ps.getStatus() == PaymentStatus.PAID) {
                paid.add(dto);
            } else if (ps.getDueDate().isBefore(today)) {
                overdue.add(dto);
            } else { // due today or in the future
                coming.add(dto);
            }
        }

        return new PaymentSummaryDto(coming, overdue, paid);
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
                            getValidTransitions(claim.getStatus()));

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
                            getValidTransitions(claim.getStatus()));
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

        // Validate claim can be rejected
        if (!claim.canTransitionTo(ClaimStatus.REJECTED)) {
            throw new IllegalStateException(
                    "Claim cannot be rejected. Current status: " + claim.getStatus() +
                            ". Valid transitions from " + claim.getStatus() + ": " +
                            getValidTransitions(claim.getStatus()));
        }

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

    private String getValidTransitions(ClaimStatus currentStatus) {
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
}
package com.example.insurance.usecases.admin.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
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
import com.example.insurance.domain.user.model.User;
import com.example.insurance.domain.user.repository.UserRepository;
import com.example.insurance.embeddable.ThirdPartyDetails;
import com.example.insurance.infrastructure.web.claim.ClaimResponseDTO;
import com.example.insurance.infrastructure.web.custommerPolicy.InsurancePolicyDto;
import com.example.insurance.shared.kernel.embeddables.Address;
import com.example.insurance.usecases.admin.controller.AdminAllPaymentsDto;
import com.example.insurance.usecases.admin.controller.AdminClaimUpdateRequest;
import com.example.insurance.usecases.admin.controller.AdminCustommersDto;
import com.example.insurance.usecases.admin.controller.AdminPolicyRequestDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final CustomerPolicyRepository customerPolicyRepository;
    private final PaymentScheduleService paymentScheduleService;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final ClaimRepository claimRepository;
    private final ClaimService claimService;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    @Override
    public List<InsurancePolicyDto> getAllPolicies() {

        System.out.println("Admin Service ===========================================>");

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
        System.out.println("DTO =============================================>" + dto.getValidityPeriod());

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

        System.out.println("Status ========================================" + dto.getStatus());
        Claim claim = claimService.findByClaimNumber(dto.getClaimId());

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

        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer Not found1"));

        return AdminMapper.toAdminCustomerDto(customer);
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

}

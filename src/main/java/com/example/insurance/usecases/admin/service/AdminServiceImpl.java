package com.example.insurance.usecases.admin.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.insurance.common.enummuration.PolicyStatus;
import com.example.insurance.domain.claim.model.Claim;
import com.example.insurance.domain.claim.repository.ClaimRepository;
import com.example.insurance.domain.claim.service.ClaimMapper;
import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.customerPolicy.model.PaymentFrequency;
import com.example.insurance.domain.customerPolicy.repository.CustomerPolicyRepository;
import com.example.insurance.domain.customerPolicy.service.PolicyMapper;
import com.example.insurance.domain.paymentSchedule.service.PaymentScheduleService;
import com.example.insurance.domain.policyBeneficiary.model.PolicyBeneficiary;
import com.example.insurance.infrastructure.web.claim.ClaimResponseDTO;
import com.example.insurance.infrastructure.web.custommerPolicy.InsurancePolicyDto;
import com.example.insurance.usecases.admin.controller.AdminPolicyRequestDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final CustomerPolicyRepository customerPolicyRepository;
    private final PaymentScheduleService paymentScheduleService;
    private final ClaimRepository claimRepository;

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

}

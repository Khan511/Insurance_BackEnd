package com.example.insurance.domain.customerPolicy.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.insurance.common.enummuration.PolicyStatus;
import com.example.insurance.common.enummuration.ProductType;
import com.example.insurance.domain.PremiumCalculation.service.PremiumCalculationService;
import com.example.insurance.domain.customer.model.Customer;
import com.example.insurance.domain.customer.model.GovernmentId;
import com.example.insurance.domain.customer.repository.CustomerRepository;
import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.customerPolicy.repository.CustomerPolicyRepository;
import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.domain.insuranceProduct.repository.InsuranceProductRepository;
import com.example.insurance.domain.paymentSchedule.model.PaymentSchedule;
import com.example.insurance.domain.paymentSchedule.service.PaymentScheduleService;
import com.example.insurance.domain.policyBeneficiary.model.PolicyBeneficiary;
import com.example.insurance.domain.user.model.User;
import com.example.insurance.domain.user.repository.UserRepository;
import com.example.insurance.infrastructure.web.custommerPolicy.BuyPolicyDto;
import com.example.insurance.infrastructure.web.custommerPolicy.GovernmentIdDto;
import com.example.insurance.infrastructure.web.custommerPolicy.InsurancePolicyDto;
import com.example.insurance.shared.kernel.embeddables.MonetaryAmount;
import com.example.insurance.shared.kernel.embeddables.PersonName;
import com.example.insurance.shared.kernel.embeddables.PolicyPeriod;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerPolicyServiceImpl implements CustomerPolicyService {

    private final PremiumCalculationService premiumCalculationService;
    private final PaymentScheduleService paymentScheduleService;
    private final InsuranceProductRepository insuranceProductRepository;
    private final CustomerPolicyRepository customerPolicyRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public void saveCustomerPolicy(BuyPolicyDto buyPolicyDto) {

        User user = userRepository.findUserByUserId(buyPolicyDto.getCustomer().getUserId())
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        InsuranceProduct product = insuranceProductRepository.findById(buyPolicyDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Insurance Product not found"));

        CustomerPolicy customerPolicy = new CustomerPolicy();

        // Set customer information
        Customer customer = new Customer();

        // Initialize the embedded name field;
        PersonName name = new PersonName();
        name.setFirstName(user.getName().getFirstName());
        name.setLastName(user.getName().getLastName());
        customer.setName(name);

        customer.setUserId(UUID.randomUUID().toString());
        customer.setEmail(user.getEmail());
        // customer.setDateOfBirth(user.getDateOfBirth());

        // Set Government ID
        GovernmentId governmentId = new GovernmentId();
        GovernmentIdDto govIdDto = buyPolicyDto.getCustomer().getGovernmentId();

        governmentId.setIdType(buyPolicyDto.getCustomer().getGovernmentId().getIdType());
        governmentId.setIssuingCountry(govIdDto.getIssuingCountry());
        governmentId.setExpirationDate(govIdDto.getExpirationDate());

        // Create original ID temporarily
        String rawIdNumber = govIdDto.getIdNumber();
        governmentId.setIdNumber(rawIdNumber);// This will auto generate masskednumber

        // Create secure hash(don't store raw ID)
        governmentId.setEncryptedHash(passwordEncoder.encode(rawIdNumber + govIdDto.getIssuingCountry()));

        // Clear sensitive data from memory ASAP
        Arrays.fill(rawIdNumber.toCharArray(), '\0');

        customer.setGovernmentId(governmentId);
        customerPolicy.setPolicyHolder(customer);

        // Create contact information
        if (buyPolicyDto.getCustomer().getContactInfo() != null) {
            customer.setContactInfo(ContactInfoMapper.toEntity(buyPolicyDto.getCustomer().getContactInfo()));
        }

        customerPolicy.setUser(user);
        customerPolicy.setPolicyNumber(product.getPolicyNumber());
        customerPolicy.setPolicyHolder(customer);
        customerPolicy.setProduct(product);
        customerPolicy.setStatus(PolicyStatus.ACTIVE);

        // Set coverage period
        PolicyPeriod coveragePeriod = new PolicyPeriod();
        coveragePeriod.setEffectiveDate(buyPolicyDto.getCoveragePeriod().getEffectiveDate());
        coveragePeriod.setExpirationDate(buyPolicyDto.getCoveragePeriod().getExpirationDate());

        customerPolicy.setCoveragePeriod(coveragePeriod);

        // Calculate premium based on product type and risk factors
        Map<String, Object> riskFactors = extractRiskFactors(buyPolicyDto, user);
        MonetaryAmount premium = premiumCalculationService.calculatePremium(product, riskFactors);
        customerPolicy.setPremium(premium);

        // Generate payment schedule
        String frequency = determinePaymentFrequency(product.getProductType()); // Or get from DTO
        List<PaymentSchedule> paymentSchedules = paymentScheduleService.generatePaymentSchedule(customerPolicy,
                frequency);
        customerPolicy.setPaymentSchedules(paymentSchedules);

        // Add policy to customer(establishes bidirectional relationship)
        customer.addPolicy(customerPolicy); // Sets policyHolder automatically

        // Create beneficiaries WITH policy association
        List<PolicyBeneficiary> beneficiaries = buyPolicyDto.getBeneficiaries().stream()
                .map(dto -> {
                    PolicyBeneficiary b = PolicyMapper.mapToBeneficiaryEntity(dto);
                    b.setCustomerPolicy(customerPolicy);
                    return b;
                }).toList();

        // Set the list to policy (triggers cascade when saved)
        customerPolicy.setBeneficiaries(beneficiaries);

        // Save customer (cascades to policy and beneficiaries)
        customerRepository.save(customer);
    }

    @Override
    public List<InsurancePolicyDto> getAllPoliciesOfUser(String userId) {
        List<CustomerPolicy> customerPolicies = customerPolicyRepository.findByUser_UserId(userId);

        return customerPolicies.stream()
                .map(PolicyMapper::toDto)
                .toList();
    }

    @Override
    public InsurancePolicyDto getInsuranceDetails(String policyId, String userId) {
        Long policyIdLong;

        try {
            policyIdLong = Long.parseLong(policyId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid policy ID format");
        }

        Optional<CustomerPolicy> policyOpt = customerPolicyRepository.findByUser_UserIdAndId(userId, policyIdLong);

        CustomerPolicy policy = policyOpt.orElseThrow(() -> new RuntimeException("Policy not found for user"));

        return PolicyMapper.toDto(policy);

    }

    @Override
    public List<CustomerPolicy> findByUserId(String userId) {
        List<CustomerPolicy> allPolicies = customerPolicyRepository.findByUser_UserId(userId);

        return allPolicies;
    }

    private Map<String, Object> extractRiskFactors(BuyPolicyDto buyPolicyDto, User user) {
        Map<String, Object> riskFactors = new HashMap<>();

        // Extract risk factors based on product type
        // You'll need to add these fields to your BuyPolicyDto

        // For auto insurance
        if (buyPolicyDto.getVehicleValue() != null) {
            riskFactors.put("vehicleValue", buyPolicyDto.getVehicleValue());
        }
        if (buyPolicyDto.getDrivingExperience() != null) {
            riskFactors.put("drivingExperience", buyPolicyDto.getDrivingExperience());
        }

        // For life insurance
        if (user.getDateOfBirth() != null) {
            int age = calculateAge(user.getDateOfBirth());
            riskFactors.put("age", age);
        }
        if (buyPolicyDto.getHealthCondition() != null) {
            riskFactors.put("healthCondition", buyPolicyDto.getHealthCondition());
        }

        // For home insurance
        if (buyPolicyDto.getPropertyValue() != null) {
            riskFactors.put("propertyValue", buyPolicyDto.getPropertyValue());
        }
        if (buyPolicyDto.getPropertyLocation() != null) {
            riskFactors.put("propertyLocation", buyPolicyDto.getPropertyLocation());
        }

        return riskFactors;
    }

    private String determinePaymentFrequency(ProductType productType) {
        // Set default payment frequency based on product type
        switch (productType) {
            case AUTO:
                return "MONTHLY";
            case LIFE:
                return "ANNUAL";
            case PROPERTY:
                return "QUARTERLY";
            default:
                return "MONTHLY";
        }
    }

    private int calculateAge(LocalDate birthDate) {
        return java.time.Period.between(birthDate, java.time.LocalDate.now()).getYears();
    }
}

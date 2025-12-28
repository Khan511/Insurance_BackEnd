package com.example.insurance.domain.customerPolicy.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.insurance.common.enummuration.PolicyStatus;
import com.example.insurance.common.enummuration.ProductType;
import com.example.insurance.domain.PremiumCalculation.service.PremiumCalculationService;
import com.example.insurance.domain.customer.model.Customer;
import com.example.insurance.domain.customer.model.GovernmentId;
import com.example.insurance.domain.customer.repository.CustomerRepository;
import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.customerPolicy.model.PaymentFrequency;
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
import com.example.insurance.infrastructure.web.premiumCalculation.PremiumCalculationRequest;
import com.example.insurance.infrastructure.web.premiumCalculation.PremiumCalculationResponse;
import com.example.insurance.shared.kernel.embeddables.MonetaryAmount;
import com.example.insurance.shared.kernel.embeddables.PersonName;
import com.example.insurance.shared.kernel.embeddables.PolicyPeriod;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

        // Check if customer already exists for this user
        Customer customer = customerRepository.findByUserId(user.getUserId())
                .orElseGet(() -> {

                    // Set customer information
                    Customer newCustomer = new Customer();

                    // Initialize the embedded name field;
                    PersonName name = new PersonName();
                    name.setFirstName(user.getName().getFirstName());
                    name.setLastName(user.getName().getLastName());
                    newCustomer.setName(name);

                    String fullName = user.getName().getFirstName() + " "
                            + user.getName().getLastName();

                    newCustomer.setUserId(user.getUserId());
                    newCustomer.setEmail(user.getEmail());
                    newCustomer.setDateOfBirth(user.getDateOfBirth());

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

                    newCustomer.setGovernmentId(governmentId);
                    // customerPolicy.setPolicyHolder(customer);

                    // Create contact information
                    if (buyPolicyDto.getCustomer().getContactInfo() != null) {
                        newCustomer.setContactInfo(
                                ContactInfoMapper.toEntity(buyPolicyDto.getCustomer().getContactInfo()));
                    }

                    return newCustomer;
                });

        customerPolicy.setUser(user);
        customerPolicy.setPolicyNumber(product.getPolicyNumber());
        customerPolicy.setPolicyHolder(customer);
        customerPolicy.setProduct(product);
        customerPolicy.setCreatedBy(user.getName().getFirstName() + " " + user.getName().getLastName());

        // Determine status based on effective date
        LocalDate effectiveDate = buyPolicyDto.getCoveragePeriod().getEffectiveDate();
        LocalDate currentDate = LocalDate.now();
        customerPolicy.setStatus(effectiveDate.isAfter(currentDate) ? PolicyStatus.INACTIVE : PolicyStatus.ACTIVE);

        // Set coverage period
        PolicyPeriod coveragePeriod = new PolicyPeriod();
        coveragePeriod.setEffectiveDate(buyPolicyDto.getCoveragePeriod().getEffectiveDate());
        coveragePeriod.setExpirationDate(buyPolicyDto.getCoveragePeriod().getExpirationDate());

        customerPolicy.setCoveragePeriod(coveragePeriod);

        // Calculate premium based on product type and risk factors
        PremiumCalculationRequest premiumRequest = new PremiumCalculationRequest();
        premiumRequest.setProductId(buyPolicyDto.getProductId());
        premiumRequest.setRiskFactors(extractRiskFactors(buyPolicyDto, product.getProductType()));
        premiumRequest.setPaymentFrequency(buyPolicyDto.getPaymentFrequency());

        PremiumCalculationResponse premiumCalculation = premiumCalculationService.calculatePremium(premiumRequest,
                user.getEmail());

        // Store annual premium
        MonetaryAmount premium = new MonetaryAmount(
                premiumCalculation.getAmount(),
                premiumCalculation.getCurrency());

        customerPolicy.setPremium(premium);
        customerPolicy.setPaymentFrequency(buyPolicyDto.getPaymentFrequency());

        if (customerPolicy.getPaymentSchedules() == null || customerPolicy.getPaymentSchedules().isEmpty()) {
            List<PaymentSchedule> paymentSchedules = paymentScheduleService.generatePaymentSchedule(customerPolicy,
                    buyPolicyDto.getPaymentFrequency());
            customerPolicy.setPaymentSchedules(paymentSchedules);
        } else {
            log.warn("Payment schedules already exist for policy. Skipping generation.");
        }

        // Add policy to customer(establishes bidirectional relationship)
        customer.addPolicy(customerPolicy);

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

    @Transactional
    public void changePaymentFrequency(Long policyId, PaymentFrequency newFrequency) {

        CustomerPolicy policy = customerPolicyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        // update the payment frequency
        policy.setPaymentFrequency(newFrequency);

        // Regenerate payment schedules
        List<PaymentSchedule> newSchedules = paymentScheduleService.generatePaymentSchedule(policy,
                newFrequency);

        // clear existing schedules and set new onces
        if (policy.getPaymentSchedules() != null) {
            policy.getPaymentSchedules().clear();
        }

        policy.setPaymentSchedules(newSchedules);

        customerPolicyRepository.save(policy);
    }

    @Override
    public List<InsurancePolicyDto> getAllPoliciesOfUser(String userId) {
        List<CustomerPolicy> customerPolicies = customerPolicyRepository.findByUser_UserId(userId);

        // Also sort payment schedules within each policy by due date
        customerPolicies.forEach(policy -> {
            if (policy.getPaymentSchedules() != null) {
                policy.getPaymentSchedules().sort(Comparator.comparing(PaymentSchedule::getDueDate));
            }
        });
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

    private Map<String, Object> extractRiskFactors(BuyPolicyDto buyPolicyDto, ProductType productType) {
        Map<String, Object> riskFactors = new HashMap<>();

        switch (productType) {
            case AUTO:
                if (buyPolicyDto.getVehicleValue() != null) {
                    riskFactors.put("vehicleValue", buyPolicyDto.getVehicleValue());
                }
                if (buyPolicyDto.getDrivingExperience() != null) {
                    riskFactors.put("drivingExperience", buyPolicyDto.getDrivingExperience());
                }
                break;
            case LIFE:
                if (buyPolicyDto.getHealthCondition() != null) {
                    riskFactors.put("healthCondition", buyPolicyDto.getHealthCondition());
                }
                break;
            case PROPERTY:
                if (buyPolicyDto.getPropertyValue() != null) {
                    riskFactors.put("propertyValue", buyPolicyDto.getPropertyValue());
                }
                if (buyPolicyDto.getPropertyLocation() != null) {
                    riskFactors.put("propertyLocation", buyPolicyDto.getPropertyLocation());
                }
                break;
            default:
                // Handle other product types if needed
                break;
        }

        return riskFactors;
    }

}

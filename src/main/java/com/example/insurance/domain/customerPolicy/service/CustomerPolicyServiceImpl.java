package com.example.insurance.domain.customerPolicy.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.insurance.common.enummuration.PolicyStatus;
import com.example.insurance.domain.customer.model.Customer;
import com.example.insurance.domain.customer.model.GovernmentId;
import com.example.insurance.domain.customer.repository.CustomerRepository;
import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.customerPolicy.repository.CustomerPolicyRepository;
import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.domain.insuranceProduct.repository.InsuranceProductRepository;
import com.example.insurance.domain.policyBeneficiary.model.PolicyBeneficiary;
import com.example.insurance.domain.user.model.User;
import com.example.insurance.domain.user.repository.UserRepository;
import com.example.insurance.infrastructure.web.custommerPolicy.BuyPolicyDto;
import com.example.insurance.infrastructure.web.custommerPolicy.GovernmentIdDto;
import com.example.insurance.infrastructure.web.custommerPolicy.InsurancePolicyDto;
import com.example.insurance.shared.kernel.dtos.InsuraceProductDto;
import com.example.insurance.shared.kernel.embeddables.MonetaryAmount;
import com.example.insurance.shared.kernel.embeddables.PersonName;
import com.example.insurance.shared.kernel.embeddables.PolicyPeriod;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerPolicyServiceImpl implements CustomerPolicyService {

    private final InsuranceProductRepository insuranceProductRepository;
    private final CustomerPolicyRepository customerPolicyRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public void saveCustomerPolicy(BuyPolicyDto buyPolicyDto) {

        System.out.println("BuyPolicy Number: " + buyPolicyDto.getPolicyNumber());

        User user = userRepository.findUserByUserId(buyPolicyDto.getCustomer().getUserId())
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        InsuranceProduct product = insuranceProductRepository.findById(buyPolicyDto.getPolicyId())
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

        // Set premium (simplified)
        MonetaryAmount premium = new MonetaryAmount();
        premium.setAmount(BigDecimal.valueOf(0));
        premium.setCurrency("DKK");
        customerPolicy.setPremium(premium);

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
}

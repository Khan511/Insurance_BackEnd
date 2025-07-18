package com.example.insurance.domain.customerPolicy.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
import com.example.insurance.infrastructure.web.insuranceProduct.InsuraceProductDto;
import com.example.insurance.shared.kernel.embeddables.PersonName;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerPolicyServiceImpl implements CustomerPolicyService {

    private final InsuranceProductRepository insuranceProductRepository;
    private final CustomerPolicyRepository customerPolicyRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void saveCustomerPolicy(BuyPolicyDto buyPolicyDto) {

        System.out.println("============================================policyId" + buyPolicyDto.getPolicyId());
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

        // Add Beneficiaries
        List<PolicyBeneficiary> beneficiaries = buyPolicyDto.getBeneficiaries().stream()
                .map((policy) -> PolicyMapper.mapToBeneficiaryEntity(policy)).toList();

        customerPolicy.setBeneficiaries(beneficiaries);
        customer.addPolicy(customerPolicy);

        customerRepository.save(customer);

        customerPolicyRepository.save(customerPolicy);

    }

}

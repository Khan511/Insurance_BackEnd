package com.example.insurance.domain.customerPolicy.service;

import java.util.Arrays;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.insurance.domain.customer.model.Customer;
import com.example.insurance.domain.customer.model.GovernmentId;
import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.customerPolicy.repository.CustomerPolicyRepository;
import com.example.insurance.infrastructure.web.insurancePolicy.BuyPolicyDto;
import com.example.insurance.infrastructure.web.insurancePolicy.GovernmentIdDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerPolicyServiceImpl implements CustomerPolicyService {

    private final CustomerPolicyRepository customerPolicyRepository;
    private final PasswordEncoder passwordEncoder;

    public void saveCustomerPolicy(BuyPolicyDto buyPolicyDto) {

        CustomerPolicy customerPolicy = new CustomerPolicy();

        // Set customer information
        Customer customer = new Customer();
        customer.getName().setFirstName(buyPolicyDto.getCustomer().getFirstName());
        customer.getName().setLastName(buyPolicyDto.getCustomer().getLastName());
        customer.setUserId(UUID.randomUUID().toString());
        customer.setEmail(buyPolicyDto.getCustomer().getEmail());
        customer.setDateOfBirth(buyPolicyDto.getCustomer().getDateOfBirth());

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

        customer.addPolicy(customerPolicy);

        customerPolicyRepository.save(customerPolicy);

    }

}

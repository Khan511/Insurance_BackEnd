package com.example.insurance.domain.customerPolicy.service;

import java.util.UUID;
import org.springframework.stereotype.Service;
import com.example.insurance.common.enummuration.UserStatus;
import com.example.insurance.domain.customer.model.ContactInfo;
import com.example.insurance.domain.customer.model.Customer;
import com.example.insurance.domain.customer.model.GovernmentId;
import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.customerPolicy.repository.CustomerPolicyRepository;
import com.example.insurance.infrastructure.web.insurancePolicy.AddressMapper;
import com.example.insurance.infrastructure.web.insurancePolicy.BuyPolicyDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerPolicyServiceImpl implements CustomerPolicyService {

    private final CustomerPolicyRepository customerPolicyRepository;

    public CustomerPolicy buyPolicy(BuyPolicyDto buyPolicyDto) {

        // Create Custommer
        Customer customer = new Customer();
        customer.setUserId(UUID.randomUUID().toString());
        customer.setEmail(buyPolicyDto.getEmail());
        customer.getName().setFirstName(buyPolicyDto.getFirstName());
        customer.getName().setLastName(buyPolicyDto.getLastName());
        customer.setStatus(UserStatus.ACTIVE);
        customer.setDateOfBirth(buyPolicyDto.getDateOfBirth());

        // 2. Set Government ID
        GovernmentId governmentId = new GovernmentId();
        governmentId.setIdType(buyPolicyDto.getGovernmentIdDto().getIdType());
        governmentId.setIdNumber(buyPolicyDto.getGovernmentIdDto().getIdNumber());
        governmentId.setIssuingCountry(buyPolicyDto.getGovernmentIdDto().getIssuingCountry());
        governmentId.setExpirationDate(buyPolicyDto.getGovernmentIdDto().getExpirationDate());
        governmentId.setVerificationStatus(GovernmentId.VerificationStatus.PENDING);

        customer.setGovernmentId(governmentId);

        // 3. Set Contact Info
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setPhone(buyPolicyDto.getContactInfoDto().getPhone());
        contactInfo.setAlternatePhone(buyPolicyDto.getContactInfoDto().getAlternatePhone());
        contactInfo.setPrimaryAddress(AddressMapper.fromDto(buyPolicyDto.getContactInfoDto().getPrimaryAddress()));
        contactInfo.setBillingAddress(AddressMapper.fromDto(buyPolicyDto.getContactInfoDto().getBillingAddress()));
        customer.setContactInfo(contactInfo);

        // 4. Create CustomerPolicy
        CustomerPolicy customerPolicy = new CustomerPolicy();

        return null;
    }

}

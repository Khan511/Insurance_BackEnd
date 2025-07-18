package com.example.insurance.domain.customerPolicy.service;

import com.example.insurance.domain.customer.model.ContactInfo;
import com.example.insurance.infrastructure.web.custommerPolicy.ContactInfoDto;

public class ContactInfoMapper {

    public static ContactInfo toEntity(ContactInfoDto dto) {
        ContactInfo contactInfo = new ContactInfo();

        contactInfo.setPhone(dto.getPhone());
        contactInfo.setAlternatePhone(dto.getAlternatePhone());

        if (dto.getPrimaryAddress() != null) {
            contactInfo.setPrimaryAddress(AddressMapper.toEntity(dto.getPrimaryAddress()));
        }
        if (dto.getBillingAddress() != null) {
            contactInfo.setBillingAddress(AddressMapper.toEntity(dto.getBillingAddress()));
        }

        return contactInfo;
    }
}

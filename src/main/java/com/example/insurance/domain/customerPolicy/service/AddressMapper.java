package com.example.insurance.domain.customerPolicy.service;

import com.example.insurance.infrastructure.web.custommerPolicy.AddressDto;
import com.example.insurance.shared.kernel.embeddables.Address;

public class AddressMapper {
    public static Address toEntity(AddressDto dto) {
        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());

        return address;
    }
}

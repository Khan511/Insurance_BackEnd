package com.example.insurance.infrastructure.web.custommerPolicy;

import com.example.insurance.shared.kernel.embeddables.Address;

public class AddressMapper {

    public static Address fromDto(AddressDto dto) {
        if (dto == null)
            return null;

        Address address = new Address();

        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        // address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());
        return address;
    }
}
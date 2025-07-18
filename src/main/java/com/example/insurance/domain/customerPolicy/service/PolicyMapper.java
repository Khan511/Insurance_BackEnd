package com.example.insurance.domain.customerPolicy.service;

import com.example.insurance.common.enummuration.Relationship;
import com.example.insurance.domain.policyBeneficiary.model.PolicyBeneficiary;
import com.example.insurance.embeddable.BeneficiaryDetails;
import com.example.insurance.infrastructure.web.custommerPolicy.BeneficiaryDto;

public class PolicyMapper {
    public static PolicyBeneficiary mapToBeneficiaryEntity(BeneficiaryDto dto) {

        PolicyBeneficiary beneficiary = new PolicyBeneficiary();
        beneficiary.setDetails(mapBeneficiaryDetails(dto));
        return beneficiary;
    }

    private static BeneficiaryDetails mapBeneficiaryDetails(BeneficiaryDto dto) {
        BeneficiaryDetails details = new BeneficiaryDetails();

        details.setFullLegalname(dto.getName());
        details.setRelationship(Relationship.valueOf(dto.getRelationship()));
        details.setDateOfBirth(dto.getDateOfBirth());
        return details;
    };

}

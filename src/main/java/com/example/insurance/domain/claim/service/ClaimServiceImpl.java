package com.example.insurance.domain.claim.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.insurance.common.enummuration.ClaimDocumentType;
import com.example.insurance.common.enummuration.IncidentType;
import com.example.insurance.domain.claim.model.Claim;
import com.example.insurance.domain.claim.model.IncidentDetails;
import com.example.insurance.domain.claim.repository.ClaimRepository;
import com.example.insurance.domain.claimDocuments.model.ClaimDocuments;
import com.example.insurance.domain.claimDocuments.service.ClaimDocumentsService;
import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.domain.insuranceProduct.service.InsuranceProductService;
import com.example.insurance.embeddable.ThirdPartyDetails;
import com.example.insurance.infrastructure.web.claim.ClaimSubmissionDTO;
import com.example.insurance.infrastructure.web.claim.DocumentAttachmentDTO;
import com.example.insurance.infrastructure.web.claim.IncidentDetailsDTO;
import com.example.insurance.shared.kernel.embeddables.Address;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final InsuranceProductService insuranceProductService;
    private final ClaimDocumentsService claimDocumentsService;

    public void submitClaim(ClaimSubmissionDTO claimSubmissionDTO) {

        InsuranceProduct product = insuranceProductService
                .getInsuranceProductByPolicyNumber(claimSubmissionDTO.getPolicyNumber());

        // Create and populate Claim entity
        Claim claim = new Claim();

        // Set Basic info
        claim.setPolicyNumber(claimSubmissionDTO.getPolicyNumber());
        claim.setClaimType(ClaimDocumentType.valueOf(claimSubmissionDTO.getClaimType()));

        // Map and set document attachments
        claim.setIncidentDetails(mapIncidentDetails(claimSubmissionDTO.getIncidentDetails()));

        // // Map and set document attachments
        // claim.setAttachedDocuments(mapDocumentAttachments(claimSubmissionDTO.getDocuments()));

        // Set relationShip with insurace product
        claim.setInsuranceProduct(product);

        // First save the claim to get an ID
        Claim savedClaim = claimRepository.save(claim);

        // Save documents using the ClaimDocuemtnService
        claimDocumentsService.saveClaimDocuments(claimSubmissionDTO.getDocuments(), savedClaim);
    }

    private IncidentDetails mapIncidentDetails(IncidentDetailsDTO dto) {
        IncidentDetails details = new IncidentDetails();

        // Set basic info
        details.setIncidentDateTime(dto.getIncidentDateTime());
        details.setType(IncidentType.valueOf(dto.getType()));
        details.setDescription(dto.getDescription());
        details.setPoliceReportNumber(dto.getPoliceReportNumber());
        details.setThirdPartyInvolved(dto.getThirdPartyInvolved());

        // Map Address
        Address address = new Address();
        address.setStreet(dto.getLocation().getStreet());
        address.setCity(dto.getLocation().getCity());
        address.setPostalCode(dto.getLocation().getPostalCode());
        address.setCountry(dto.getLocation().getCountry());

        details.setLocation(address);

        // Map third party details if applicable
        if (dto.getThirdPartyInvolved() && dto.getThirdPartyInvolved() != null) {
            ThirdPartyDetails thirdParty = new ThirdPartyDetails();
            thirdParty.setName(dto.getThirdPartyDetails().getName());
            thirdParty.setContactInfo(dto.getThirdPartyDetails().getContactInfo());
            thirdParty.setInsuranceInfo(dto.getThirdPartyDetails().getInsuranceInfo());

            details.setThirdPartyDetails(thirdParty);
        }

        return details;

    }

    private List<ClaimDocuments> mapDocumentAttachments(List<DocumentAttachmentDTO> dtos, Claim claim) {

        return dtos.stream()
                .map(dto -> {
                    return new ClaimDocuments(
                            UUID.fromString(dto.getStorageId()),
                            dto.getStorageBucket(),
                            dto.getOriginalFileName(),
                            dto.getContentType(),
                            dto.getSha256Checksum(),
                            ClaimDocumentType.RequiredDocument.valueOf(dto.getDocumentType()), claim);
                }).collect(Collectors.toList());
    }

}
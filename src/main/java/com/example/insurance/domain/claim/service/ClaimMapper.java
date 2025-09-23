
package com.example.insurance.domain.claim.service;

import java.util.List;

import com.example.insurance.domain.claim.model.Claim;
import com.example.insurance.domain.claim.model.IncidentDetails;
import com.example.insurance.infrastructure.web.claim.ClaimResponseDTO;
import com.example.insurance.infrastructure.web.claim.DocumentAttachmentDTO;
import com.example.insurance.infrastructure.web.claim.IncidentDetailsDTO;

public class ClaimMapper {

    public static ClaimResponseDTO mapToDto(Claim claim) {
        ClaimResponseDTO dto = new ClaimResponseDTO();

        dto.setPolicyNumber(claim.getPolicyNumber());
        dto.setClaimType(claim.getClaimType().name());
        dto.setClaimNumber(claim.getClaimNumber());
        dto.setStatus(claim.getStatus());
        dto.setAmount(claim.getAmount());
        // Map IncidentDetails
        IncidentDetails incident = claim.getIncidentDetails();
        if (incident != null) {
            IncidentDetailsDTO incidentDto = new IncidentDetailsDTO();
            incidentDto.setIncidentDateTime(incident.getIncidentDateTime());
            incidentDto.setType(incident.getType().name());
            incidentDto.setDescription(incident.getDescription());
            incidentDto.setPoliceReportNumber(incident.getPoliceReportNumber());
            incidentDto.setThirdPartyInvolved(incident.isThirdPartyInvolved());

            // Map Address
            if (incident.getLocation() != null) {
                incidentDto.setLocation(new com.example.insurance.infrastructure.web.claim.AddressDTO(
                        incident.getLocation().getStreet(),
                        incident.getLocation().getCity(),
                        incident.getLocation().getPostalCode(),
                        incident.getLocation().getCountry()));
            }

            // Third party
            if (Boolean.TRUE.equals(incident.isThirdPartyInvolved()) && incident.getThirdPartyDetails() != null) {
                incidentDto.setThirdPartyDetails(
                        new com.example.insurance.infrastructure.web.claim.ThirdPartyDetailsDTO(
                                incident.getThirdPartyDetails().getName(),
                                incident.getThirdPartyDetails().getContactInfo(),
                                incident.getThirdPartyDetails().getInsuranceInfo()));
            }
            dto.setIncidentDetails(incidentDto);
        }

        // Map documents
        if (claim.getAttachedDocuments() != null) {
            List<DocumentAttachmentDTO> docDtos = claim.getAttachedDocuments().stream()
                    .map(doc -> {
                        DocumentAttachmentDTO d = new DocumentAttachmentDTO();
                        d.setStorageId(doc.getStorageId().toString());
                        d.setStorageBucket(doc.getStorageBucket());
                        d.setOriginalFileName(doc.getOriginalFilename());
                        d.setContentType(doc.getContentType());
                        d.setSha256Checksum(doc.getSha256Checksum());
                        d.setDocumentType(doc.getDocumentType().name());
                        d.setFileUrl(doc.getFileUrl());
                        d.setFileSize(doc.getFileSize());
                        d.setFileKey(doc.getFileKey());
                        d.setUploadedAt(doc.getUploadedAt());
                        return d;
                    })
                    .toList();
            dto.setDocuments(docDtos);
        }

        return dto;
    }
}
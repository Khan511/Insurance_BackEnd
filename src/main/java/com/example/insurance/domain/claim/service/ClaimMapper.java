
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

        // Basic claim information
        dto.setId(claim.getId());
        dto.setPolicyNumber(claim.getPolicyNumber());
        dto.setClaimNumber(claim.getClaimNumber());
        dto.setClaimType(claim.getClaimType() != null ? claim.getClaimType().name() : null);
        dto.setStatus(claim.getStatus());
        // dto.setAmount(claim.getAmount());
        dto.setApprovedAmount(claim.getApprovedAmount());

        // Date fields
        if (claim.getSubmissionDate() != null) {
            dto.setSubmissionDate(claim.getSubmissionDate());
        }
        if (claim.getApprovedDate() != null) {
            dto.setApprovedDate(claim.getApprovedDate());
        }
        if (claim.getRejectedDate() != null) {
            dto.setRejectedDate(claim.getRejectedDate());
        }
        if (claim.getPaidDate() != null) {
            dto.setPaidDate(claim.getPaidDate());
        }
        if (claim.getClosedDate() != null) {
            dto.setClosedDate(claim.getClosedDate());
        }
        // Admin fields
        dto.setProcessedBy(claim.getProcessedBy());
        dto.setRejectionReason(claim.getRejectionReason());

        // Calculated fields from entity methods
        dto.setProcessingDays(claim.getProcessingDays());
        dto.setIsOpen(claim.isOpen());
        dto.setIsProcessed(claim.isProcessed());

        // payment-related fields
        dto.setPaymentStatus(claim.getPaymentStatus() != null ? claim.getPaymentStatus().name() : "NOT_PAID");
        dto.setApprovalNotes(claim.getApprovalNotes());
        dto.setPaidBy(claim.getPaidBy());
        dto.setPaymentReference(claim.getPaymentReference());
        dto.setPaymentNotes(claim.getPaymentNotes());
        // CanBePaid flag for UI
        dto.setCanBePaid(claim.canBePaid());
        // isFullyPaid flag
        dto.setIsFullyPaid(claim.isFullyPaid());

        // Status transition permissions (useful for UI buttons)
        if (claim.getStatus() != null) {
            dto.setCanBeApproved(claim.canTransitionTo(com.example.insurance.shared.enummuration.ClaimStatus.APPROVED));
            dto.setCanBeRejected(claim.canTransitionTo(com.example.insurance.shared.enummuration.ClaimStatus.REJECTED));
            dto.setCanBePaid(claim.canTransitionTo(com.example.insurance.shared.enummuration.ClaimStatus.PAID));
        }

        // Map IncidentDetails
        IncidentDetails incident = claim.getIncidentDetails();
        if (incident != null) {
            IncidentDetailsDTO incidentDto = new IncidentDetailsDTO();
            incidentDto.setIncidentDateTime(incident.getIncidentDateTime());
            incidentDto.setType(incident.getType() != null ? incident.getType().name() : null);
            incidentDto.setDescription(incident.getDescription());
            incidentDto.setPoliceReportNumber(incident.getPoliceReportNumber());
            incidentDto.setThirdPartyInvolved(incident.isThirdPartyInvolved());
            incidentDto.setClaimAmount(incident.getClaimAmount());

            // Map Address
            if (incident.getLocation() != null) {
                incidentDto.setLocation(new com.example.insurance.infrastructure.web.claim.AddressDTO(
                        incident.getLocation().getStreet(),
                        incident.getLocation().getCity(),
                        incident.getLocation().getPostalCode(),
                        incident.getLocation().getCountry()));
            }

            // Third party details
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
                        d.setStorageId(doc.getStorageId() != null ? doc.getStorageId().toString() : null);
                        d.setStorageBucket(doc.getStorageBucket());
                        d.setOriginalFileName(doc.getOriginalFilename());
                        d.setContentType(doc.getContentType());
                        d.setSha256Checksum(doc.getSha256Checksum());
                        d.setDocumentType(doc.getDocumentType() != null ? doc.getDocumentType().name() : null);
                        d.setFileUrl(doc.getFileUrl());
                        d.setFileSize(doc.getFileSize());
                        d.setFileKey(doc.getFileKey());
                        d.setUploadedAt(doc.getUploadedAt());
                        return d;
                    })
                    .toList();
            dto.setDocuments(docDtos);
        }

        // Map product information (optional but useful)
        if (claim.getInsuranceProduct() != null) {
            dto.setProductName(claim.getInsuranceProduct().getDisplayName());
            dto.setProductCode(claim.getInsuranceProduct().getProductCode());
        }

        // Map customer information (optional)
        if (claim.getUser() != null) {
            dto.setCustomerName(
                    claim.getUser().getName().getFirstName() + " " + claim.getUser().getName().getLastName());
            dto.setCustomerEmail(claim.getUser().getEmail());
        }
        return dto;
    }

    // Additional mapper for admin/list views (with less detail)
    public static ClaimResponseDTO mapToSummaryDto(Claim claim) {
        ClaimResponseDTO dto = new ClaimResponseDTO();

        dto.setId(claim.getId());
        dto.setPolicyNumber(claim.getPolicyNumber());
        dto.setClaimNumber(claim.getClaimNumber());
        dto.setStatus(claim.getStatus());
        // dto.setAmount(claim.getAmount());
        dto.setApprovedAmount(claim.getApprovedAmount());
        dto.setSubmissionDate(claim.getSubmissionDate());
        dto.setApprovedDate(claim.getApprovedDate());
        dto.setRejectedDate(claim.getRejectedDate());
        dto.setPaidDate(claim.getPaidDate());
        dto.setClosedDate(claim.getClosedDate());
        dto.setProcessedBy(claim.getProcessedBy());
        dto.setProcessingDays(claim.getProcessingDays());
        dto.setIsOpen(claim.isOpen());
        dto.setIsProcessed(claim.isProcessed());

        // Product info
        if (claim.getInsuranceProduct() != null) {
            dto.setProductName(claim.getInsuranceProduct().getDisplayName());
            dto.setProductCode(claim.getInsuranceProduct().getProductCode());
        }

        // Customer info
        if (claim.getUser() != null) {
            dto.setCustomerName(
                    claim.getUser().getName().getFirstName() + " " + claim.getUser().getName().getLastName());
            dto.setCustomerEmail(claim.getUser().getEmail());
        }
        return dto;
    }

    // For admin panel with all details
    public static ClaimResponseDTO mapToAdminDto(Claim claim) {
        ClaimResponseDTO dto = new ClaimResponseDTO();

        // Basic claim information
        dto.setId(claim.getId());
        dto.setPolicyNumber(claim.getPolicyNumber());
        dto.setClaimNumber(claim.getClaimNumber());
        dto.setClaimType(claim.getClaimType() != null ? claim.getClaimType().name() : null);
        dto.setStatus(claim.getStatus());
        // dto.setAmount(claim.getAmount());
        dto.setApprovedAmount(claim.getApprovedAmount());

        // Date fields
        if (claim.getSubmissionDate() != null) {
            dto.setSubmissionDate(claim.getSubmissionDate());
        }
        if (claim.getApprovedDate() != null) {
            dto.setApprovedDate(claim.getApprovedDate());
        }
        if (claim.getRejectedDate() != null) {
            dto.setRejectedDate(claim.getRejectedDate());
        }
        if (claim.getPaidDate() != null) {
            dto.setPaidDate(claim.getPaidDate());
        }
        if (claim.getClosedDate() != null) {
            dto.setClosedDate(claim.getClosedDate());
        }
        // Admin fields
        dto.setProcessedBy(claim.getProcessedBy());
        dto.setRejectionReason(claim.getRejectionReason());

        // Calculated fields from entity methods
        dto.setProcessingDays(claim.getProcessingDays());
        dto.setIsOpen(claim.isOpen());
        dto.setIsProcessed(claim.isProcessed());

        // payment-related fields
        dto.setPaymentStatus(claim.getPaymentStatus() != null ? claim.getPaymentStatus().name() : "NOT_PAID");
        dto.setApprovalNotes(claim.getApprovalNotes());
        dto.setPaidBy(claim.getPaidBy());
        dto.setPaymentReference(claim.getPaymentReference());
        dto.setPaymentNotes(claim.getPaymentNotes());
        // CanBePaid flag for UI
        dto.setCanBePaid(claim.canBePaid());
        // isFullyPaid flag
        dto.setIsFullyPaid(claim.isFullyPaid());

        // Status transition permissions (useful for UI buttons)
        if (claim.getStatus() != null) {
            dto.setCanBeApproved(claim.canTransitionTo(com.example.insurance.shared.enummuration.ClaimStatus.APPROVED));
            dto.setCanBeRejected(claim.canTransitionTo(com.example.insurance.shared.enummuration.ClaimStatus.REJECTED));
            dto.setCanBePaid(claim.canTransitionTo(com.example.insurance.shared.enummuration.ClaimStatus.PAID));
        }

        // Map IncidentDetails
        IncidentDetails incident = claim.getIncidentDetails();
        if (incident != null) {
            IncidentDetailsDTO incidentDto = new IncidentDetailsDTO();
            incidentDto.setIncidentDateTime(incident.getIncidentDateTime());
            incidentDto.setType(incident.getType() != null ? incident.getType().name() : null);
            incidentDto.setDescription(incident.getDescription());
            incidentDto.setPoliceReportNumber(incident.getPoliceReportNumber());
            incidentDto.setThirdPartyInvolved(incident.isThirdPartyInvolved());
            incidentDto.setClaimAmount(incident.getClaimAmount());

            // Map Address
            if (incident.getLocation() != null) {
                incidentDto.setLocation(new com.example.insurance.infrastructure.web.claim.AddressDTO(
                        incident.getLocation().getStreet(),
                        incident.getLocation().getCity(),
                        incident.getLocation().getPostalCode(),
                        incident.getLocation().getCountry()));
            }

            // Third party details
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
                        d.setStorageId(doc.getStorageId() != null ? doc.getStorageId().toString() : null);
                        d.setStorageBucket(doc.getStorageBucket());
                        d.setOriginalFileName(doc.getOriginalFilename());
                        d.setContentType(doc.getContentType());
                        d.setSha256Checksum(doc.getSha256Checksum());
                        d.setDocumentType(doc.getDocumentType() != null ? doc.getDocumentType().name() : null);
                        d.setFileUrl(doc.getFileUrl());
                        d.setFileSize(doc.getFileSize());
                        d.setFileKey(doc.getFileKey());
                        d.setUploadedAt(doc.getUploadedAt());
                        return d;
                    })
                    .toList();
            dto.setDocuments(docDtos);
        }

        // Map product information (optional but useful)
        if (claim.getInsuranceProduct() != null) {
            dto.setProductName(claim.getInsuranceProduct().getDisplayName());
            dto.setProductCode(claim.getInsuranceProduct().getProductCode());
        }

        // Map customer information (optional)
        if (claim.getUser() != null) {
            dto.setCustomerName(
                    claim.getUser().getName().getFirstName() + " " + claim.getUser().getName().getLastName());
            dto.setCustomerEmail(claim.getUser().getEmail());
        }
        return dto;

    }
}
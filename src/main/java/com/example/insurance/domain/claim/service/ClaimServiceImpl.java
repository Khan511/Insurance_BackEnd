// package com.example.insurance.domain.claim.service;

// import java.math.BigDecimal;
// import java.util.List;
// // import java.util.UUID;
// // import java.util.stream.Collectors;

// import org.springframework.stereotype.Service;

// import com.example.insurance.common.enummuration.ClaimDocumentType;
// import com.example.insurance.common.enummuration.ClaimStatus;
// import com.example.insurance.common.enummuration.IncidentType;
// import com.example.insurance.domain.claim.model.Claim;
// import com.example.insurance.domain.claim.model.IncidentDetails;
// import com.example.insurance.domain.claim.repository.ClaimRepository;
// // import com.example.insurance.domain.claimDocuments.model.ClaimDocuments;
// import com.example.insurance.domain.claimDocuments.service.ClaimDocumentsService;
// import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
// import com.example.insurance.domain.insuranceProduct.service.InsuranceProductService;
// import com.example.insurance.domain.user.model.User;
// import com.example.insurance.domain.user.repository.UserRepository;
// import com.example.insurance.embeddable.ThirdPartyDetails;
// import com.example.insurance.global.config.CustomUserDetails;
// import com.example.insurance.infrastructure.web.claim.ClaimResponseDTO;
// import com.example.insurance.infrastructure.web.claim.ClaimSubmissionDTO;
// // import com.example.insurance.infrastructure.web.claim.DocumentAttachmentDTO;
// import com.example.insurance.infrastructure.web.claim.IncidentDetailsDTO;
// import com.example.insurance.shared.kernel.embeddables.Address;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class ClaimServiceImpl implements ClaimService {

//     private final ClaimRepository claimRepository;
//     private final UserRepository userRepository;
//     private final InsuranceProductService insuranceProductService;
//     private final ClaimDocumentsService claimDocumentsService;
//     private final ClaimIdGenerator claimIdGenerator;

//     public void submitClaim(ClaimSubmissionDTO claimSubmissionDTO, CustomUserDetails customUserDetails) {

//         User user = userRepository.findUserByUserId(customUserDetails.getUserEntity().getUserId())
//                 .orElseThrow(() -> new RuntimeException("User Not Found"));

//         InsuranceProduct product = insuranceProductService
//                 .getInsuranceProductByPolicyNumber(claimSubmissionDTO.getPolicyNumber());

//         // Create and populate Claim entity
//         Claim claim = new Claim();

//         // Set Basic info
//         claim.setClaimNumber(claimIdGenerator.generateUniqueClaimId());
//         claim.setStatus(ClaimStatus.PENDING);

//         claim.setUser(user);
//         claim.setPolicyNumber(claimSubmissionDTO.getPolicyNumber());
//         claim.setClaimType(ClaimDocumentType.valueOf(claimSubmissionDTO.getClaimType()));

//         // Map and set document attachments
//         claim.setIncidentDetails(mapIncidentDetails(claimSubmissionDTO.getIncidentDetails()));

//         // // Map and set document attachments
//         // claim.setAttachedDocuments(mapDocumentAttachments(claimSubmissionDTO.getDocuments()));

//         // Set relationShip with insurace product
//         claim.setInsuranceProduct(product);

//         // First save the claim to get an ID
//         Claim savedClaim = claimRepository.save(claim);

//         // Save documents using the ClaimDocuemtnService
//         claimDocumentsService.saveClaimDocuments(claimSubmissionDTO.getDocuments(), savedClaim);
//     }

//     private IncidentDetails mapIncidentDetails(IncidentDetailsDTO dto) {
//         IncidentDetails details = new IncidentDetails();

//         // Set basic info
//         details.setIncidentDateTime(dto.getIncidentDateTime());
//         details.setType(IncidentType.valueOf(dto.getType()));
//         details.setDescription(dto.getDescription());
//         details.setPoliceReportNumber(dto.getPoliceReportNumber());
//         details.setThirdPartyInvolved(dto.getThirdPartyInvolved());

//         // Map Address
//         Address address = new Address();
//         address.setStreet(dto.getLocation().getStreet());
//         address.setCity(dto.getLocation().getCity());
//         address.setPostalCode(dto.getLocation().getPostalCode());
//         address.setCountry(dto.getLocation().getCountry());

//         details.setLocation(address);

//         // Map third party details if applicable
//         if (dto.getThirdPartyInvolved() && dto.getThirdPartyInvolved() != null) {
//             ThirdPartyDetails thirdParty = new ThirdPartyDetails();
//             thirdParty.setName(dto.getThirdPartyDetails().getName());
//             thirdParty.setContactInfo(dto.getThirdPartyDetails().getContactInfo());
//             thirdParty.setInsuranceInfo(dto.getThirdPartyDetails().getInsuranceInfo());

//             details.setThirdPartyDetails(thirdParty);
//         }
//         return details;
//     }

//     @Override
//     public List<ClaimResponseDTO> getAllClaimOfUser(String userId) {
//         List<Claim> allClaims = claimRepository.findByUser_UserId(userId);

//         return allClaims.stream()
//                 .map(claim -> ClaimMapper.mapToDto(claim))
//                 .toList();
//     }

//     public void processClaim(Long claimId, BigDecimal amount, ClaimStatus status) {
//         Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));

//         claim.setAmount(amount);
//         claim.setStatus(status);

//         claimRepository.save(claim);
//     }

//     public Claim findByClaimNumber(String claimNumber) {
//         return claimRepository.findByClaimNumber(claimNumber)
//                 .orElseThrow(() -> new RuntimeException("Claim not found!"));
//     }

// }

package com.example.insurance.domain.claim.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.insurance.common.enummuration.ClaimDocumentType;
import com.example.insurance.common.enummuration.ClaimStatus;
import com.example.insurance.common.enummuration.IncidentType;
import com.example.insurance.domain.claim.model.Claim;
import com.example.insurance.domain.claim.model.IncidentDetails;
import com.example.insurance.domain.claim.repository.ClaimRepository;
import com.example.insurance.domain.claimDocuments.service.ClaimDocumentsService;
import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.domain.insuranceProduct.service.InsuranceProductService;
import com.example.insurance.domain.user.model.User;
import com.example.insurance.domain.user.repository.UserRepository;
import com.example.insurance.embeddable.ThirdPartyDetails;
import com.example.insurance.global.config.CustomUserDetails;
import com.example.insurance.infrastructure.web.claim.ClaimResponseDTO;
import com.example.insurance.infrastructure.web.claim.ClaimSubmissionDTO;
import com.example.insurance.infrastructure.web.claim.IncidentDetailsDTO;
import com.example.insurance.shared.kernel.embeddables.Address;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;
    private final InsuranceProductService insuranceProductService;
    private final ClaimDocumentsService claimDocumentsService;
    private final ClaimIdGenerator claimIdGenerator;

    @Transactional
    public void submitClaim(ClaimSubmissionDTO claimSubmissionDTO, CustomUserDetails customUserDetails) {
        User user = userRepository.findUserByUserId(customUserDetails.getUserEntity().getUserId())
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        InsuranceProduct product = insuranceProductService
                .getInsuranceProductByPolicyNumber(claimSubmissionDTO.getPolicyNumber());

        // Create and populate Claim entity
        Claim claim = new Claim();

        // Set Basic info
        claim.setClaimNumber(claimIdGenerator.generateUniqueClaimId());
        claim.setStatus(ClaimStatus.PENDING);

        claim.setUser(user);
        claim.setPolicyNumber(claimSubmissionDTO.getPolicyNumber());
        claim.setClaimType(ClaimDocumentType.valueOf(claimSubmissionDTO.getClaimType()));

        // Map and set incident details
        claim.setIncidentDetails(mapIncidentDetails(claimSubmissionDTO.getIncidentDetails()));

        // Set relationship with insurance product
        claim.setInsuranceProduct(product);

        // First save the claim to get an ID
        Claim savedClaim = claimRepository.save(claim);

        // Save documents using the ClaimDocumentService
        if (claimSubmissionDTO.getDocuments() != null) {
            claimDocumentsService.saveClaimDocuments(claimSubmissionDTO.getDocuments(), savedClaim);
        }
    }

    private IncidentDetails mapIncidentDetails(IncidentDetailsDTO dto) {
        IncidentDetails details = new IncidentDetails();

        // Set basic info
        details.setIncidentDateTime(dto.getIncidentDateTime());
        details.setType(IncidentType.valueOf(dto.getType()));
        details.setDescription(dto.getDescription());
        details.setPoliceReportNumber(dto.getPoliceReportNumber());
        details.setThirdPartyInvolved(dto.getThirdPartyInvolved());

        // Set the claimed amount from DTO (if provided)
        if (dto.getClaimAmount() != null) {
            details.setClaimAmount(dto.getClaimAmount());
        }

        // Map Address
        Address address = new Address();
        address.setStreet(dto.getLocation().getStreet());
        address.setCity(dto.getLocation().getCity());
        address.setPostalCode(dto.getLocation().getPostalCode());
        address.setCountry(dto.getLocation().getCountry());

        details.setLocation(address);

        // Map third party details if applicable
        if (dto.getThirdPartyInvolved() && dto.getThirdPartyDetails() != null) {
            ThirdPartyDetails thirdParty = new ThirdPartyDetails();
            thirdParty.setName(dto.getThirdPartyDetails().getName());
            thirdParty.setContactInfo(dto.getThirdPartyDetails().getContactInfo());
            thirdParty.setInsuranceInfo(dto.getThirdPartyDetails().getInsuranceInfo());

            details.setThirdPartyDetails(thirdParty);
        }
        return details;
    }

    @Override
    public List<ClaimResponseDTO> getAllClaimOfUser(String userId) {
        List<Claim> allClaims = claimRepository.findByUser_UserId(userId);

        return allClaims.stream()
                .map(claim -> ClaimMapper.mapToDto(claim))
                .toList();
    }

    public Claim findByClaimNumber(String claimNumber) {
        return claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new RuntimeException("Claim not found!"));
    }

    // ========== NEW BUSINESS METHODS USING CLAIM ENTITY METHODS ==========

    // @Transactional
    // @Override
    // public void approveClaim(Long claimId, String adminUsername, BigDecimal
    // approvedAmount) {
    // Claim claim = claimRepository.findById(claimId)
    // .orElseThrow(() -> new RuntimeException("Claim not found"));

    // // Validate transition
    // if (!claim.canTransitionTo(ClaimStatus.APPROVED)) {
    // throw new IllegalStateException(
    // String.format("Cannot approve claim from current status: %s",
    // claim.getStatus()));
    // }

    // // Use the entity's business method
    // claim.approve(adminUsername, approvedAmount);
    // claimRepository.save(claim);
    // }

    // @Transactional
    // @Override
    // public void rejectClaim(Long claimId, String adminUsername, String
    // rejectionReason) {
    // Claim claim = claimRepository.findById(claimId)
    // .orElseThrow(() -> new RuntimeException("Claim not found"));

    // // Validate transition
    // if (!claim.canTransitionTo(ClaimStatus.REJECTED)) {
    // throw new IllegalStateException(
    // String.format("Cannot reject claim from current status: %s",
    // claim.getStatus()));
    // }

    // // Use the entity's business method
    // claim.reject(adminUsername, rejectionReason);
    // claimRepository.save(claim);
    // }

    // @Transactional
    // @Override
    // public void markClaimAsPaid(Long claimId, String adminUsername) {
    // Claim claim = claimRepository.findById(claimId)
    // .orElseThrow(() -> new RuntimeException("Claim not found"));

    // // Validate transition (claim must be APPROVED to be paid)
    // if (!claim.canTransitionTo(ClaimStatus.PAID)) {
    // throw new IllegalStateException(
    // String.format("Cannot mark claim as paid from current status: %s",
    // claim.getStatus()));
    // }

    // // Use the entity's business method
    // claim.markAsPaid(adminUsername);
    // claimRepository.save(claim);
    // }

    // @Transactional
    // @Override
    // public void updateClaimStatus(Long claimId, ClaimStatus newStatus, String
    // adminUsername, String reason) {
    // Claim claim = claimRepository.findById(claimId)
    // .orElseThrow(() -> new RuntimeException("Claim not found"));

    // // Validate transition
    // if (!claim.canTransitionTo(newStatus)) {
    // throw new IllegalStateException(
    // String.format("Cannot transition claim from %s to %s", claim.getStatus(),
    // newStatus));
    // }

    // // Handle different status transitions
    // switch (newStatus) {
    // case APPROVED:
    // // For approval, we need approved amount - this should come from request
    // throw new IllegalArgumentException("Use approveClaim method for approval with
    // amount");
    // case REJECTED:
    // claim.reject(adminUsername, reason);
    // break;
    // case PAID:
    // claim.markAsPaid(adminUsername);
    // break;
    // case UNDER_REVIEW:
    // // For simple status changes, just set the status
    // claim.setStatus(newStatus);
    // claim.setProcessedBy(adminUsername);
    // break;
    // default:
    // // For other statuses, just update
    // claim.setStatus(newStatus);
    // }

    // claimRepository.save(claim);
    // }

    // @Override
    // public Claim getClaimDetails(Long claimId) {
    // return claimRepository.findById(claimId)
    // .orElseThrow(() -> new RuntimeException("Claim not found"));
    // }

    // @Override
    // public List<Claim> getAllClaims() {
    // return claimRepository.findAll();
    // }

    // @Override
    // public List<Claim> getOpenClaims() {
    // return claimRepository.findAll().stream()
    // .filter(Claim::isOpen)
    // .toList();
    // }

    // @Override
    // public List<Claim> getProcessedClaims() {
    // return claimRepository.findAll().stream()
    // .filter(Claim::isProcessed)
    // .toList();
    // }

    // @Override
    // public long getAverageProcessingTime() {
    // List<Claim> processedClaims = claimRepository.findAll().stream()
    // .filter(claim -> claim.getClosedDate() != null)
    // .toList();

    // if (processedClaims.isEmpty()) {
    // return 0;
    // }

    // long totalDays = processedClaims.stream()
    // .mapToLong(Claim::getProcessingDays)
    // .sum();

    // return totalDays / processedClaims.size();
    // }

    // ========== DEPRECATE OLD METHOD ==========

    /**
     * @deprecated Use specific methods like approveClaim, rejectClaim,
     *             markClaimAsPaid
     */
    // @Deprecated
    // public void processClaim(Long claimId, BigDecimal amount, ClaimStatus status)
    // {
    // Claim claim = claimRepository.findById(claimId)
    // .orElseThrow(() -> new RuntimeException("Claim not found"));

    // // Old method - just sets amount and status
    // // This doesn't use the business methods and doesn't set dates properly
    // claim.setAmount(amount);
    // claim.setStatus(status);

    // claimRepository.save(claim);
    // }
}
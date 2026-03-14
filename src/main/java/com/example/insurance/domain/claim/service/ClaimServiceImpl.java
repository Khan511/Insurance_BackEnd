
package com.example.insurance.domain.claim.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.insurance.domain.claim.model.Claim;
import com.example.insurance.domain.claim.model.IncidentDetails;
import com.example.insurance.domain.claim.repository.ClaimRepository;
import com.example.insurance.domain.claimDocuments.service.ClaimDocumentsService;
import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.domain.insuranceProduct.service.InsuranceProductService;
import com.example.insurance.domain.user.model.User;
import com.example.insurance.domain.user.repository.UserRepository;
import com.example.insurance.global.config.CustomUserDetails;
import com.example.insurance.infrastructure.web.claim.ClaimResponseDTO;
import com.example.insurance.infrastructure.web.claim.ClaimSubmissionDTO;
import com.example.insurance.infrastructure.web.claim.IncidentDetailsDTO;
import com.example.insurance.shared.enummuration.ClaimDocumentType;
import com.example.insurance.shared.enummuration.ClaimStatus;
import com.example.insurance.shared.enummuration.IncidentType;
import com.example.insurance.shared.kernel.embeddables.Address;
import com.example.insurance.shared.kernel.embeddables.ThirdPartyDetails;

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

}
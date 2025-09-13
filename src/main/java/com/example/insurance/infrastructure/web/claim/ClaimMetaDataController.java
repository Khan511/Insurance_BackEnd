package com.example.insurance.infrastructure.web.claim;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.insurance.common.enummuration.ClaimDocumentType;
import com.example.insurance.domain.claim.service.ClaimService;
import com.example.insurance.global.config.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/claim-metadata")
public class ClaimMetaDataController {

    private final ClaimService claimService;

    @GetMapping("/claim-types")
    public List<ClaimMetadataDTO> getClaimDocumentTypes() {
        return Arrays.stream(ClaimDocumentType.values())
                .map(type -> new ClaimMetadataDTO(type.name(), type.getDisplayName())).collect(Collectors.toList());
    }

    @GetMapping("/incident-types")
    public List<String> getIncidentTypesForClaimType(@RequestParam String claimType) {
        ClaimDocumentType type = ClaimDocumentType.valueOf(claimType);
        return type.getSupportedIncidentTypes().stream().map(Enum::name).collect(Collectors.toList());
    }

    @GetMapping("/required-documents")
    public List<DocumentTypeDTO> getDocumentTypesForClaimType(
            @RequestParam String claimType) {

        ClaimDocumentType type = ClaimDocumentType.valueOf(claimType);
        return type.getRequiredDocuments().stream()
                .map(doc -> new DocumentTypeDTO(doc.name(), doc.getDisplayName()))
                .collect(Collectors.toList());
    }

    @PostMapping("/submit-claim")
    public ResponseEntity<?> submitClaim(@Valid @RequestBody ClaimSubmissionDTO claimData,
            @AuthenticationPrincipal CustomUserDetails user) {
        claimService.submitClaim(claimData, user);

        return ResponseEntity.ok(Map.of("message", "Claim submitted successfully"));
    }

    @GetMapping("/get-all-claims/{userId}")
    public ResponseEntity<?> getAllClaimsOfUser(@PathVariable String userId) {
        List<ClaimResponseDTO> claims = claimService.getAllClaimOfUser(userId);

        return ResponseEntity.ok(Map.of("claim", claims));
    }

}

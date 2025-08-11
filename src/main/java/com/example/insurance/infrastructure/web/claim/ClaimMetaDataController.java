package com.example.insurance.infrastructure.web.claim;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.insurance.common.enummuration.ClaimDocumentType;

@RestController
@RequestMapping("/api/claim-metadata")
public class ClaimMetaDataController {

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
}

package com.example.insurance.global.validation;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.insurance.common.enummuration.ClaimDocumentType;
import com.example.insurance.domain.claim.model.Claim;
import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.embeddable.DocumentAttachment;

public class ClaimValidator {
        public static void validateClaim(Claim claim, CustomerPolicy policy) {
                // 1. Validate claim type against policy
                if (!policy.getProduct().getAllowedClaimTypes().contains(claim.getClaimType())) {
                        throw new RuntimeException(
                                        String.format("Policy %s doesn't cover claim type %s",
                                                        policy.getPolicyNumber(), claim.getClaimType().name()));
                }

                // 2. Validate required documents
                Set<ClaimDocumentType.RequiredDocument> attachedDocTypes = claim.getAttachedDocuments()
                                .stream()
                                .map(DocumentAttachment::getDocumentType)
                                .collect(Collectors.toSet());

                Set<ClaimDocumentType.RequiredDocument> requiredDocs = new HashSet<>(
                                claim.getClaimType().getRequiredDocuments());

                requiredDocs.removeAll(attachedDocTypes);

                if (!requiredDocs.isEmpty()) {
                        throw new RuntimeException(
                                        "Missing required documents: " +
                                                        requiredDocs.stream()
                                                                        .map(Enum::name)
                                                                        .collect(Collectors.joining(", ")));
                }
        }
}
// public class ClaimValidator {
// public static void validateClaim(Claim claim, CustomerPolicy policy) {
// if
// (!policy.getProduct().getAllowedClaimTypes().contains(claim.getClaimType()))
// {
// throw new RuntimeException(
// "Policy doesn't cover this claim type");
// }
// if (claim.getClaimType().getRequiredDocuments().size() >
// claim.getAttachedDocuments().size()) {
// throw new RuntimeException("Required documents are not attached");
// }
// }
// }

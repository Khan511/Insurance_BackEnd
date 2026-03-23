package com.example.insurance.domain.claim.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.example.insurance.domain.claim.model.Claim;
import com.example.insurance.domain.claim.repository.ClaimRepository;
import com.example.insurance.domain.claimDocuments.service.ClaimDocumentsService;
import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.domain.insuranceProduct.service.InsuranceProductService;
import com.example.insurance.domain.user.model.User;
import com.example.insurance.domain.user.repository.UserRepository;
import com.example.insurance.global.config.CustomUserDetails;
import com.example.insurance.infrastructure.web.claim.AddressDTO;
import com.example.insurance.infrastructure.web.claim.ClaimSubmissionDTO;
import com.example.insurance.infrastructure.web.claim.IncidentDetailsDTO;
import com.example.insurance.shared.enummuration.ClaimDocumentType;
import com.example.insurance.shared.enummuration.IncidentType;

class ClaimServiceImplTest {

    private ClaimRepository claimRepository;
    private UserRepository userRepository;
    private InsuranceProductService insuranceProductService;
    private ClaimDocumentsService claimDocumentsService;
    private ClaimIdGenerator claimIdGenerator;
    private ClaimServiceImpl claimService;

    @BeforeEach
    void setUp() {
        claimRepository = mock(ClaimRepository.class);
        userRepository = mock(UserRepository.class);
        insuranceProductService = mock(InsuranceProductService.class);
        claimDocumentsService = mock(ClaimDocumentsService.class);
        claimIdGenerator = mock(ClaimIdGenerator.class);

        claimService = new ClaimServiceImpl(
                claimRepository,
                userRepository,
                insuranceProductService,
                claimDocumentsService,
                claimIdGenerator);
    }

    @Test
    void submitClaimBuildsFinalClaimNumberFromPersistedDatabaseId() {
        User authenticatedUser = new User();
        authenticatedUser.setUserId("user-123");

        User persistedUser = new User();
        persistedUser.setUserId("user-123");

        InsuranceProduct product = new InsuranceProduct();

        when(userRepository.findUserByUserId("user-123")).thenReturn(Optional.of(persistedUser));
        when(insuranceProductService.getInsuranceProductByPolicyNumber("POL-100")).thenReturn(product);
        when(claimIdGenerator.generateTemporaryClaimId()).thenReturn("TMP-claim");
        when(claimIdGenerator.generateClaimId(42L)).thenReturn("CLM-000042");
        AtomicReference<String> claimNumberUsedForInitialInsert = new AtomicReference<>();
        when(claimRepository.saveAndFlush(any(Claim.class))).thenAnswer(invocation -> {
            Claim claim = invocation.getArgument(0);
            claimNumberUsedForInitialInsert.set(claim.getClaimNumber());
            claim.setId(42L);
            return claim;
        });
        when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> invocation.getArgument(0));

        claimService.submitClaim(buildClaimSubmission(), new CustomUserDetails(authenticatedUser));

        verify(claimRepository).saveAndFlush(any(Claim.class));
        assertEquals("TMP-claim", claimNumberUsedForInitialInsert.get());

        ArgumentCaptor<Claim> secondSaveCaptor = ArgumentCaptor.forClass(Claim.class);
        verify(claimRepository).save(secondSaveCaptor.capture());
        assertEquals(Long.valueOf(42L), secondSaveCaptor.getValue().getId());
        assertEquals("CLM-000042", secondSaveCaptor.getValue().getClaimNumber());

        verify(claimIdGenerator).generateTemporaryClaimId();
        verify(claimIdGenerator).generateClaimId(42L);
        verifyNoInteractions(claimDocumentsService);
    }

    private ClaimSubmissionDTO buildClaimSubmission() {
        ClaimSubmissionDTO dto = new ClaimSubmissionDTO();
        dto.setPolicyNumber("POL-100");
        dto.setClaimType(ClaimDocumentType.AUTOMOBILE_COLLISION.name());

        IncidentDetailsDTO incidentDetails = new IncidentDetailsDTO();
        incidentDetails.setIncidentDateTime(LocalDateTime.now().minusDays(1));
        incidentDetails.setType(IncidentType.AUTO_COLLISION.name());
        incidentDetails.setDescription("Rear-end collision");
        incidentDetails.setLocation(new AddressDTO("Street 1", "Oslo", "0001", "NO"));
        incidentDetails.setThirdPartyInvolved(false);

        dto.setIncidentDetails(incidentDetails);
        return dto;
    }
}

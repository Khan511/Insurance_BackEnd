package com.example.insurance.domain.customer.model;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class GovernmentId {

    public enum IdType {
        PASSPORT,
        DRIVERS_LICENSE,
        NATIONAL_ID,
        SSN,
        TAX_ID,
        RESIDENCE_PERMIT
    }

    public enum VerificationStatus {
        PENDING,
        VERIFIED,
        EXPIRED,
        SUSPENDED,
        REJECTED,
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "id_type", nullable = false)
    @NotNull(message = "ID type is required")
    private IdType idType;

    @Column(name = "id_number", nullable = false, length = 50)
    @NotBlank(message = "ID number is required")
    @Size(min = 4, max = 50, message = "ID number must be 4-50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\-]+$", message = "Invalid ID number format")
    private String idNumber;

    @Column(name = "issuing_country", length = 3)
    @Size(min = 2, max = 3, message = "Country code must be 2 or 3 letters long, like 'US' or 'IND'.")
    @Pattern(regexp = "^[A-Z]{2,3}$", message = "Only uppercase letters allowed in country code.")
    private String issuingCountry;

    @Column(name = "expiration_date")
    @FutureOrPresent(message = "Expiration date must be in future")
    private LocalDate expirationDate;

    @Column(name = "verification_status")
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    // For duplicate detection, Store data in hash
    @Column(name = "encrypted_hash", length = 256)
    private String encryptedHash;

    // To display a partially hidden version of the ID number in your
    // UI:("56******78")
    @Column(name = "masked_number", length = 50)
    private String maskedNumber;

    // Business logic mthods
    public String generateMaskedNumber() {
        if (idNumber == null || idNumber.length() < 4)
            return "****";

        int keepChars = Math.min(2, idNumber.length() / 4);
        String prefix = idNumber.substring(0, keepChars);
        String suffix = idNumber.substring(idNumber.length() - keepChars);

        return prefix + "*".repeat(6) + suffix;
    }

    public boolean isVerified() {
        return verificationStatus == VerificationStatus.VERIFIED;
    }

    public IdType getIdType() {
        return idType;
    }

    public void setType(IdType idType) {
        this.idType = idType;
        if (this.maskedNumber == null) {
            this.maskedNumber = generateMaskedNumber();
        }
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;

        this.maskedNumber = generateMaskedNumber();
    }

}

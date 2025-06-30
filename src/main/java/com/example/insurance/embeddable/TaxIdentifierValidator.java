package com.example.insurance.embeddable;

import com.example.insurance.common.enummuration.NordicCountry;

import jakarta.persistence.Embeddable;

@Embeddable
public class TaxIdentifierValidator {

    // Countyr specific validation logic
    public static boolean isValidTaxId(NordicCountry country, String taxId) {

        if (taxId == null)
            return false;

        switch (country) {
            case DENMARK:
                return isValidDanishCpr(taxId);
            case SWEDEN:
                return isValidSwedishPersonnummer(taxId);

            case NORWAY:
                isValidNorwegianFodselsnummer(taxId);
            default:
                return false;
        }
    }

    private static boolean isValidDanishCpr(String cpr) {
        // CPR format: DDMMYY-XXXX
        return cpr.matches("^\\d{6}-\\d{4}$");
    }

    private static boolean isValidSwedishPersonnummer(String pnr) {
        // Format: YYMMDD-XXXX or YYYYMMDD-XXXX
        return pnr.matches("^(\\d{6}|\\d{8})-\\d{4}$");
    }

    private static boolean isValidNorwegianFodselsnummer(String fnr) {
        // Format: DDMMYYXXXXX (11 digits)
        return fnr.matches("^\\d{11}$");
    }

}

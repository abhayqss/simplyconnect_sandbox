package com.scnsoft.eldermark.hl7v2.processor.insurance;

public class InsuranceUtils {

    public static String buildInsuranceCode(String insuranceCompanyName) {
        var upperCased = insuranceCompanyName.toUpperCase();
        //replace non alphanumeric with _
        var replaced = upperCased.replaceAll("[^A-Z\\d]", "_");
        return replaced;
    }
}

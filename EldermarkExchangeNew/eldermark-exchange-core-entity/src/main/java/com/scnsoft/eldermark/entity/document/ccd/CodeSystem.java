package com.scnsoft.eldermark.entity.document.ccd;

import org.apache.commons.collections4.MapUtils;

import java.util.Map;

public enum CodeSystem {

    /**
     * Logical Observation Identifier Names and Codes<br/>
     * The international standard for identifying health measurements, observations, and documents.<br/>
     * OID = "2.16.840.1.113883.6.1"
     *
     * @see <a href="https://loinc.org/">SNOMED International</a>
     */
    LOINC("LOINC", "2.16.840.1.113883.6.1", "Logical Observation Identifier Names and Codes"),

    /**
     * SNOMED Clinical Terms - a language for health terms.<br/>
     * SNOMED CT is a clinically validated, semantically rich, controlled vocabulary.<br/>
     * OID = "2.16.840.1.113883.6.96"
     *
     * @see <a href="http://www.snomed.org/">SNOMED International</a>
     */
    SNOMED_CT("SNOMED-CT", "2.16.840.1.113883.6.96", "SNOMED Clinical Terms"),

    /**
     * Codes for the Role class hierarchy.
     * The values in this hierarchy, represent a Role which is an association or relationship between two entities - the entity that
     * plays the role and the entity that scopes the role. Roles names are derived from the name of the playing entity in that role.
     */
    ROLE_CLASS(null, "2.16.840.1.113883.5.110", "Role Class"),

    /**
     * A set of codes further specifying the kind of Role; specific classification codes for further qualifying RoleClass codes.
     */
    ROLE_CODE(null, "2.16.840.1.113883.5.111", "Role Code"),

    /**
     * The gender of a person used for adminstrative purposes (as opposed to clinical gender).
     * Examples: F, M, UN.
     */
    ADMINISTRATIVE_GENDER(null, "2.16.840.1.113883.5.1", "Administrative Gender"),

    MARITAL_STATUS(null, "2.16.840.1.113993.5.2", "MaritalStatus"),

    MARITAL_STATUS_CCD_R1_1(null, "2.16.840.1.113883.5.2", "MaritalStatus"),

    RELIGIOUS_AFFILIATION(null, "2.16.840.1.113883.5.1076", "ReligiousAffiliation"),

    LANGUAGE(null, "2.16.840.1.113883.6.121", "Language"),

    /**
     * The Provider Taxonomy Code List is published (released) twice a year on July 1st and January 1st. The July publication is effective for use on October 1st and the January publication is effective for use on April 1st. The time between the publication release and the effective date is considered an implementation period to allow providers, payers and vendors an opportunity to incorporate any changes into their systems. This listing includes Active codes approved for use effective April 1st, 2003, version 3.0; and codes that are New and approved for use effective October 1st, 2003, version 3.1.
     */
    NUCC_PROVIDER_CODES("Health Care Provider Taxonomy", "2.16.840.1.113883.6.101", "Provider Taxonomy Code List"),

    /**
     * A comprehensive classification of locations and settings where healthcare services are provided. This value set is based on the NHSN location code system that has been developed over a number of years through CDCaTMs interaction with a variety of healthcare facilities and is intended to serve a variety of reporting needs where coding of healthcare service locations is required.
     */
    HEALTHCARE_SERVICE_LOCATION("HealthcareServiceLocation", "2.16.840.1.113883.6.259", "Healthcare Service Location (HL7)"),

    X12N_1336("X12N-1336", "2.16.840.1.113883.6.255.1336", "Insurance Type Code"),

    NCI_THESAURUS("NCI Thesaurus", "2.16.840.1.113883.3.26.1.1", "National Cancer Institute (NCI) Thesaurus"),

    CVX("CVX", "2.16.840.1.113883.12.292", "CDC Vaccine Code (CVX)"),

    RACE_AND_ETHNICITY_CDC("Race and Ethnicity - CDC", "2.16.840.1.113883.6.238", "Race and Ethnicity - CDC"),

    HL7_ACT_CODE("HL7ActCode", "2.16.840.1.113883.5.4", "HL7 Act Code"),
    HL7_ACT_CLASS("HL7ActClass", "2.16.840.1.113883.5.6", "HL7 Act Class"),
    HL7_ACT_STATUS(null, "2.16.840.1.113883.5.14", "HL7 Act Status"),

    RX_NORM("RxNorm", "2.16.840.1.113883.6.88", "RxNorm"),

    ICD_10_CM("ICD-10-CM", "2.16.840.1.113883.6.90", "ICD-10-CM"),

    ICD_9_CM("ICD-9-CM", "2.16.840.1.113883.6.103", "ICD-9-CM"),

    NDC("National Drug Codes", "2.16.840.1.113883.6.69", "National Drug Codes"),

    MEDISPAN_GPI("Medispan GPI", "2.16.840.1.113883.6.68", "Medispan GPI"),

    MDDX("MDDX", "2.16.840.1.113883.6.65", "Medispan Diagnostic Codes"),

    MEDICATION_PRESCRIBER_ELDERMARK_SPECIALITY("Medication Prescriber Eldermark Speciality",
            CdaConstants.EXCHANGE_CODE_SYSTEMS + ".1", "Medication Prescriber Eldermark Speciality"),

    MEDICATION_PHARMACY_TYPE("Medication Eldermark Pharmacy Type",
            CdaConstants.EXCHANGE_CODE_SYSTEMS + ".2", "Medication Eldermark Pharmacy Type"),

    DAW_PRODUCT_SELECTION("DAW Product Selection Code",
            CdaConstants.EXCHANGE_CODE_SYSTEMS + ".3", "DAW Product Selection Code"),

    PRESCRIPTION_ORIGIN("Prescription Origin Code",
            CdaConstants.EXCHANGE_CODE_SYSTEMS + ".4", "Prescription Origin Code");

    //map retired code system oids to current code systems
    public static final Map<String, CodeSystem> RETIRED_CODE_SYSTEM_OIDS = MapUtils.unmodifiableMap(Map.of(
            "2.16.840.1.113883.6.59", CVX
    ));

    private final String displayName;
    private final String oid;
    private final String fullName;

    CodeSystem(String displayName, String oid, String fullName) {
        this.displayName = displayName;
        this.oid = oid;
        this.fullName = fullName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getOid() {
        return oid;
    }

    public String getFullName() {
        return fullName;
    }
}

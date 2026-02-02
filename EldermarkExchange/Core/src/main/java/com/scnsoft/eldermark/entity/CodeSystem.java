package com.scnsoft.eldermark.entity;

/**
 * <h1>Known code systems.</h1>
 * A code system is a set of codes and their assigned meaning or interpretation.<br/>
 * <br/>
 * Code systems can be independently maintained by third party standards organizations.
 * Many of the code systems used in C-CDA – especially for administrative codes – were created by (and are maintained by) HL7.
 *
 * @author phomal
 * Created on 3/22/2017.
 */
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

    /**
     * The Provider Taxonomy Code List is published (released) twice a year on July 1st and January 1st. The July publication is effective for use on October 1st and the January publication is effective for use on April 1st. The time between the publication release and the effective date is considered an implementation period to allow providers, payers and vendors an opportunity to incorporate any changes into their systems. This listing includes Active codes approved for use effective April 1st, 2003, version 3.0; and codes that are New and approved for use effective October 1st, 2003, version 3.1.
     */
    NUCC_PROVIDER_CODES("Health Care Provider Taxonomy", "2.16.840.1.113883.6.101", "Provider Taxonomy Code List"),

    /**
     * A comprehensive classification of locations and settings where healthcare services are provided. This value set is based on the NHSN location code system that has been developed over a number of years through CDCaTMs interaction with a variety of healthcare facilities and is intended to serve a variety of reporting needs where coding of healthcare service locations is required.
     */
    HEALTHCARE_SERVICE_LOCATION("HealthcareServiceLocation", "2.16.840.1.113883.6.259", "Healthcare Service Location (HL7)"),

    X12N_1336("X12N-1336", "2.16.840.1.113883.6.255.1336", "Insurance Type Code"),

    HL7_ACT_CODE("HL7ActCode", "2.16.840.1.113883.5.4", "HL7 Act Code"),
    HL7_ACT_CLASS("HL7ActClass", "2.16.840.1.113883.5.6", "HL7 Act Class"),
    HL7_ACT_STATUS(null, "2.16.840.1.113883.5.14", "HL7 Act Status"),

    ICD_10_CM("ICD-10-CM", "2.16.840.1.113883.6.90", "International Classification of Diseases, Tenth Revision, Clinical Modification"),

    ICD_9_CN("ICD-9-CM", "2.16.840.1.113883.6.103", "International Classification of Diseases, Ninth Revision, Clinical Modification");

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


    public static CodeSystem findByDisplayName(String displayName) {
        for (CodeSystem codeSystem: CodeSystem.values()) {
            if (codeSystem.getDisplayName() != null && codeSystem.getDisplayName().equalsIgnoreCase(displayName)) {
                return codeSystem;
            }
        }
        return null;
    }
}

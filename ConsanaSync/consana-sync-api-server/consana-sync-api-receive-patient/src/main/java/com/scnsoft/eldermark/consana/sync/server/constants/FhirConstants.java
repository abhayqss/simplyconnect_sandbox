package com.scnsoft.eldermark.consana.sync.server.constants;

public class FhirConstants {

    public static final String ADMINISTRATIVE_GENDER_CODE_SYSTEM = "2.16.840.1.113883.5.1";
    public static final String MARITAL_STATUS_CODE_SYSTEM = "2.16.840.1.113993.5.2";
    public static final String V2_IDENTIFIER_TYPE = "http://hl7.org/fhir/v2/0203";
    public static final String RACE_VALUE_SET = "2.16.840.1.113883.1.11.14914";
    public static final String RACE_EXTENSION_URL = "http://xchangelabs.com/fhir-extensions/patient-race";
    public static final String ADMIT_DATE_EXTENSION_URL = "http://xchangelabs.com/fhir-extensions/patient-admit-date";
    public static final String CONSANA_LEGACY_TABLE = "CCN_CONSANA_SYNC";
    public static final String ETHNIC_GROUP_CODE_SYSTEM = "2.16.840.1.113883.6.238";
    public static final String ETHNIC_GROUP_EXTENSION_URL = "http://hl7.org/fhir/ValueSet/v3-Ethnicity";
    public static final String RELIGION_EXTENSION_URL = "http://terminology.hl7.org/ValueSet/v3-ReligiousAffiliation";
    public static final String RELIGION_CODE_SYSTEM = "2.16.840.1.113883.5.1076";
    public static final String CITIZENSHIP_URL = "http://hl7.org/fhir/StructureDefinition/patient-nationality";
    public static final String DOSE_UNIT_EXTENSION_URL = "http://xchangelabs.com/fhir-extensions/medication-unit-of-measure";
    public static final String MED_INFORMATION_LEGACY_TABLE = "Res_Medications";
    public static final String MED_DISPENSE_LEGACY_TABLE = "Res_Medications";
    public static final String RX_CUI_EXTENSION_URL = "http://xchangelabs.com/fhir-extensions/medication-rxcui";
    public static final String RX_NORM_CODE_SYSTEM_NAME = "RxNorm";
    public static final String DATE_TIME_TYPE = "DateTimeType";
    public static final String SIMPLY_CONNECT_ID_EXTENSION_URL = "http://xchangelabs.com/fhir-extensions/simply-connect-id";
    public static final String REACTION_CODE_SYSTEM = "2.16.840.1.113883.6.90";
    public static final String PROBLEM_STATUS_VALUE_SET = "2.16.840.1.113883.3.88.12.80.68";
    public static final int MAX_LENGTH_UUID = 20;

    private FhirConstants(){}

}

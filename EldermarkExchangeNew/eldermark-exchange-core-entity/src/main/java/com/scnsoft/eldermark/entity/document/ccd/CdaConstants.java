package com.scnsoft.eldermark.entity.document.ccd;

public final class CdaConstants {

    private final static String EXCHANGE_HCID = "2.16.840.1.113883.3.6492";

    public final static String EXCHANGE_INTEGRATIONS_WITHOUT_OID = EXCHANGE_HCID + ".1000";

    public static final String ID_ROOT = EXCHANGE_HCID;
    public static final String US_NPI_ROOT = "2.16.840.1.113883.4.6";

    public static final String EXCHANGE_CDA_ELEMENTS = EXCHANGE_HCID + ".11";
    public static final String EXCHANGE_CDA_IDENTIFIERS = EXCHANGE_CDA_ELEMENTS + ".1";
    public static final String EXCHANGE_TEMPLATE_IDS = EXCHANGE_CDA_IDENTIFIERS + ".1";
    public static final String EXCHANGE_PRESCRIBER_ORGANIZATION_EXT_PHARMACY_ID = EXCHANGE_CDA_IDENTIFIERS + ".2";
    public static final String EXCHANGE_ELDERMARK_LEGACY_ID = EXCHANGE_CDA_IDENTIFIERS + ".3";
    public static final String EXCHANGE_MEDICATION_PRESCRIPTION_NUMBER = EXCHANGE_CDA_IDENTIFIERS + ".4";
    public static final String EXCHANGE_HP_CLAIM_BILLING_PROVIDER_REF_ID = EXCHANGE_CDA_IDENTIFIERS + ".5";

    public static final String EXCHANGE_CODE_SYSTEMS = EXCHANGE_HCID + ".12";

    public static final String EXCHANGE_VALUE_SETS = EXCHANGE_HCID + ".13";

    private static final String TEMPLATE_ID_VERSION_1 = ".1";

    public static final String MEDICATION_ELDERMARK_PHARMACY_TEMPLATE_ID = EXCHANGE_TEMPLATE_IDS + ".1" + TEMPLATE_ID_VERSION_1;
    public static final String MEDICATION_DAW_CODE_WRAPPER_TEMPLATE_ID = EXCHANGE_TEMPLATE_IDS + ".2" + TEMPLATE_ID_VERSION_1;
    public static final String MEDICATION_RX_ORIGIN_CODE_WRAPPER_TEMPLATE_ID = EXCHANGE_TEMPLATE_IDS + ".3" + TEMPLATE_ID_VERSION_1;

    private CdaConstants() {
    }

}

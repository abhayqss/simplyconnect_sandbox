package com.scnsoft.eldermark.xds;

/**
 * Created by averazub on 8/12/2016.
 */
public class Iti42Builder {

    //IF DOCUMENT SET TO SHARED THEN
    public static final String CONFIDENTIALLY_CODE_NORMAL = "1.3.6.1.4.1.21367.2006.7.107" ;
    public static final String CONFIDENTIALLY_CODE_LOCALIZED_NORMAL = "Normal Sharing";

    public static final String CONFIDENTIALLY_CODE_NO_SHARING = "1.3.6.1.4.1.21367.2006.7.106";
    public static final String CONFIDENTIALLY_CODE_LOCALIZED_NO_SHARING = "No Sharing";

    public static final String CONTENTTYPE_CODE_CONST = "Communication";
    public static final String CONTENTTYPE_CODE_LOCALIZED_CONST = "Communication";
    public static final String LANGUAGE_CODE_CONST = "en-US";
    public static final String HEALTHCARE_FACILITY_TYPE_CODE_CONST = "Assisted Living";
    public static final String PRACTICE_SETTING_CODE_CONST = "General Medicine";

    //TODO

    public static final String FORMAT_CODE_CONST = "1.3.6.1.4.1.19376.1.5.3.1.1.2";
    public static final String FORMAT_CODE_LOCALIZED_CONST = "XDS-MS";



    public static Iti42DocumentData createIti42DocumentData(ExchangeDocumentData src, String repositoryUniqueId, String homeCommunityId) {

        Iti42DocumentData data = new Iti42DocumentData();
        data.setMimeType(src.getMimeType());
        data.setCreationTime(src.getCreateTime());
        data.setDocumentTitle(src.getDocTitle());
        data.setSize(src.getSize());

        if (!Boolean.FALSE.equals(src.getShared())) {
            data.setConfidentiallyCode(CONFIDENTIALLY_CODE_NORMAL);
            data.setConfidentiallyCodeLocalized(CONFIDENTIALLY_CODE_LOCALIZED_NORMAL);
        } else {
            data.setConfidentiallyCode(CONFIDENTIALLY_CODE_NO_SHARING);
            data.setConfidentiallyCodeLocalized(CONFIDENTIALLY_CODE_LOCALIZED_NO_SHARING);
        }
        data.setRepositoryUniqueId(repositoryUniqueId);
        data.setAssigningAuthorityId(homeCommunityId);
        data.setDocumentUUID("urn:uuid:"+src.getUuid());

        data.setSourcePatientId(src.getPatientId());
        data.setPatientId(src.getPatientId());
        data.setHash(src.getHash());
        data.setUniqueId(src.getUniqueId());


        //--- HARDCODED
        data.setLanguageCode(LANGUAGE_CODE_CONST);
        data.setContentTypeCode(CONTENTTYPE_CODE_CONST);
        data.setContentTypeCodeLocalized(CONTENTTYPE_CODE_LOCALIZED_CONST);
        data.setFormatCode(FORMAT_CODE_CONST);
        data.setFormatCodeLocalized(FORMAT_CODE_LOCALIZED_CONST);
        data.setHealthcareFacilityTypeCode(HEALTHCARE_FACILITY_TYPE_CODE_CONST);
        data.setPracticeSettingCode(PRACTICE_SETTING_CODE_CONST);

        return data;
    }

}

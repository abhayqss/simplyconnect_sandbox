package com.scnsoft.eldermark.cda.service.schema;

import java.util.Objects;

/**
 * @author phomal
 * Created on 4/9/2018.
 */
public enum DocumentType {

    // C-CDA R2.1 - NOT USED YET
    CCDA_R2_1_CCD_V3(CCDAVersion.R2, TemplateId.of("2.16.840.1.113883.10.20.22.1.2", "2015-08-01")),

    // C-CDA R2.0 - NOT USED YET
    CCDA_R2_0_CCD_V2(CCDAVersion.R2, TemplateId.of("2.16.840.1.113883.10.20.22.1.2", "2014-06-09")),

    // C-CDA R1.1
    CCDA_R1_1_CCD_V1(CCDAVersion.R1, TemplateId.of("2.16.840.1.113883.10.20.22.1.2", noExtension())),

    HL7_CCD(isNotCCDA(), TemplateId.of("2.16.840.1.113883.10.20.1", noExtension())),
    HITSP_C32(isNotCCDA(), TemplateId.of("2.16.840.1.113883.3.88.11.32.1", noExtension())),

    UNIDENTIFIED(isNotCCDA(), null);

    private final CCDAVersion ccdaVersion;
    private final TemplateId templateId;


    DocumentType(CCDAVersion ccdaVersion, TemplateId templateId) {
        this.ccdaVersion = ccdaVersion;
        this.templateId = templateId;
    }

    public static DocumentType from(String root, String extension) {
        final TemplateId templateId = TemplateId.of(root, extension);
        for (DocumentType type : values()) {
            if (DocumentType.isIdentified(type) && type.getTemplateId().equals(templateId)) {
                return type;
            }
        }

        return UNIDENTIFIED;
    }

    public static boolean isIdentified(DocumentType documentType) {
        return documentType != UNIDENTIFIED;
    }

    private static CCDAVersion isNotCCDA() {
        return null;
    }

    private static String noExtension() {
        return null;
    }

    public TemplateId getTemplateId() {
        return templateId;
    }

    public CCDAVersion getCcdaVersion() {
        return ccdaVersion;
    }

    public boolean isCCDA(CCDAVersion ccdaVersion) {
        return Objects.equals(this.ccdaVersion, ccdaVersion);
    }

}

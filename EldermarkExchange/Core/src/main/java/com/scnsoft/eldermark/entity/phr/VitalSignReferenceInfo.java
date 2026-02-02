package com.scnsoft.eldermark.entity.phr;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by averazub on 1/10/2017.
 */
@Entity
@Table(name = "VitalSignReferenceInformation")
public class VitalSignReferenceInfo extends BaseEntity {

    @Column(name="vital_sign_type_code")
    private String code;

    @Column(name="reference_info")
    private String referenceInfo;

    public VitalSignReferenceInfo() {
    }

    public VitalSignReferenceInfo(String code, String referenceInfo) {
        this.code = code;
        this.referenceInfo = referenceInfo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getReferenceInfo() {
        return referenceInfo;
    }

    public void setReferenceInfo(String referenceInfo) {
        this.referenceInfo = referenceInfo;
    }
}

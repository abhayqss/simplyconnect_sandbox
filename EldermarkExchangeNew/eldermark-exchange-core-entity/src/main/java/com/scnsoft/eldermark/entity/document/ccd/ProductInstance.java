package com.scnsoft.eldermark.entity.document.ccd;

import javax.persistence.*;

import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

/**
 * <h1>Product Instance</h1>
 * This entity represents a particular device that was placed in a patient or used as part of a procedure or other act.
 * This provides a record of the identifier and other details about the given product that was used.
 * For example, it is important to have a record that indicates not just that a hip prostheses was placed in a patient
 * but that it was a particular hip prostheses number with a unique identifier. The FDA Amendments Act specifies the
 * creation of a Unique Device Identification (UDI) System that requires the label of devices to bear a unique identifier
 * that will standardize device identification and identify the device through distribution and use. The UDI should be sent
 * in the participantRole/id.
 */
@Entity
public class ProductInstance extends LegacyIdAwareEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "device_code_id")
    private CcdCode deviceCode;

    @Column(name = "scoping_entity_id")
    private String scopingEntityId;

    @Column(name = "scoping_entity_description")
    private String scopingEntityDescription;

    @ManyToOne
    @JoinColumn(name = "scoping_entity_code_id")
    private CcdCode scopingEntityCode;

    public CcdCode getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(CcdCode deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getScopingEntityId() {
        return scopingEntityId;
    }

    public void setScopingEntityId(String scopingEntityId) {
        this.scopingEntityId = scopingEntityId;
    }

    public String getScopingEntityDescription() {
        return scopingEntityDescription;
    }

    public void setScopingEntityDescription(String scopingEntityDescription) {
        this.scopingEntityDescription = scopingEntityDescription;
    }

    public CcdCode getScopingEntityCode() {
        return scopingEntityCode;
    }

    public void setScopingEntityCode(CcdCode scopingEntityCode) {
        this.scopingEntityCode = scopingEntityCode;
    }
}

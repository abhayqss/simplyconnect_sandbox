package com.scnsoft.eldermark.entity.document.ccd;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.basic.StringLegacyIdAwareEntity;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
@Table(name = "ImmunizationMedicationInformation")
@AttributeOverride(name = "legacyId", column = @Column(name = "legacy_id", nullable = false, length = 32))
public class ImmunizationMedicationInformation extends StringLegacyIdAwareEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "code_id")
    private CcdCode code;

    @Column
    private String text;

    @Column(name = "lot_number_text")
    private String lotNumberText;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="organization_id")
    private Community manufactorer;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLotNumberText() {
        return lotNumberText;
    }

    public void setLotNumberText(String lotNumberText) {
        this.lotNumberText = lotNumberText;
    }

    public Community getManufactorer() {
        return manufactorer;
    }

    public void setManufactorer(Community manufactorer) {
        this.manufactorer = manufactorer;
    }

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }
}

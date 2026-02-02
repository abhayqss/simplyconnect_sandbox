package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
@Table(name = "ImmunizationMedicationInformation")
@AttributeOverride(name = "legacyId", column = @Column(name = "legacy_id", nullable = false, length = 32))
public class ImmunizationMedicationInformation extends StringLegacyIdAwareEntity {

    @ManyToOne
    @JoinColumn(name = "code_id")
    private CcdCode code;

    @Column
    private String text;

    @Column(name = "lot_number_text")
    private String lotNumberText;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="organization_id")
    private Organization manufactorer;

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

    public Organization getManufactorer() {
        return manufactorer;
    }

    public void setManufactorer(Organization manufactorer) {
        this.manufactorer = manufactorer;
    }

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }
}

package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
public class MedicationInformation extends LegacyTableAwareEntity {

    @ManyToOne
    @JoinColumn(name = "product_name_code_id")
    private CcdCode productNameCode;

    @Lob
    @Column(name = "product_name_text")
    private String productNameText;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="organization_id")
    private Organization manufactorer;

    public CcdCode getProductNameCode() {
        return productNameCode;
    }

    public void setProductNameCode(CcdCode productNameCode) {
        this.productNameCode = productNameCode;
    }

    public String getProductNameText() {
        return productNameText;
    }

    public void setProductNameText(String productNameText) {
        this.productNameText = productNameText;
    }

    public Organization getManufactorer() {
        return manufactorer;
    }

    public void setManufactorer(Organization manufactorer) {
        this.manufactorer = manufactorer;
    }
}

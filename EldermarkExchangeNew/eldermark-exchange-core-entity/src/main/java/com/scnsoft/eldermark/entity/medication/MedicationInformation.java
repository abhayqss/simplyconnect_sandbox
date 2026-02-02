package com.scnsoft.eldermark.entity.medication;

import com.scnsoft.eldermark.entity.basic.LegacyTableAwareEntity;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;

import javax.persistence.*;
import java.util.List;

@Entity
public class MedicationInformation extends LegacyTableAwareEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "product_name_code_id")
    private CcdCode productNameCode;

    @Lob
    @Column(name = "product_name_text")
    private String productNameText;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "organization_id")
    private Community manufactorer;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "MedicationInformation_ProductCodeTranslation",
            joinColumns = @JoinColumn(name = "medication_information_id"),
            inverseJoinColumns = @JoinColumn(name = "code_id"))
    private List<CcdCode> translationProductCodes;

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

    public Community getManufactorer() {
        return manufactorer;
    }

    public void setManufactorer(Community manufactorer) {
        this.manufactorer = manufactorer;
    }

    public List<CcdCode> getTranslationProductCodes() {
        return translationProductCodes;
    }

    public void setTranslationProductCodes(List<CcdCode> translationProductCodes) {
        this.translationProductCodes = translationProductCodes;
    }
}

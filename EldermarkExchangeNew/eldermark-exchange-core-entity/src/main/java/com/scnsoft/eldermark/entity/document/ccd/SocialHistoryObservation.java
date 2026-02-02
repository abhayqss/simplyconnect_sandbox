package com.scnsoft.eldermark.entity.document.ccd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
public class SocialHistoryObservation extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name="type_code_id")
    private CcdCode type;

    /**
     * Coded as {@code <observation> <code> <originalText> ... </originalText> </code> </observation>}
     */
    @Column(name = "free_text")
    private String freeText;

    /**
     * Coded as {@code <observation> <value type="ST"> ... </value> </observation>}
     */
    @Column(name = "free_text_value")
    private String freeTextValue;

    @ManyToOne
    @JoinColumn(name="value_code_id")
    private CcdCode value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "social_history_id", nullable = false)
    private SocialHistory socialHistory;

    public CcdCode getType() {
        return type;
    }

    public void setType(CcdCode type) {
        this.type = type;
    }

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public CcdCode getValue() {
        return value;
    }

    public void setValue(CcdCode value) {
        this.value = value;
    }

    public String getFreeTextValue() {
        return freeTextValue;
    }

    public void setFreeTextValue(String freeTextValue) {
        this.freeTextValue = freeTextValue;
    }

    public SocialHistory getSocialHistory() {
        return socialHistory;
    }

    public void setSocialHistory(SocialHistory socialHistory) {
        this.socialHistory = socialHistory;
    }
}

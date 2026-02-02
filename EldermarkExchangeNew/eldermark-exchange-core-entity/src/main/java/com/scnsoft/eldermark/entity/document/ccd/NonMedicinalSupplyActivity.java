package com.scnsoft.eldermark.entity.document.ccd;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.scnsoft.eldermark.entity.basic.BasicEntity;

@Entity
public class NonMedicinalSupplyActivity extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "mood_code", length = 8, nullable = false)
    private String moodCode;

    @Column(name = "status_code", length = 50)
    private String statusCode;

    @Column(name = "effective_time_high")
    private Date effectiveTimeHigh;

    private BigDecimal quantity;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_instance_id")
    private ProductInstance productInstance;

    public String getMoodCode() {
        return moodCode;
    }

    public void setMoodCode(String moodCode) {
        this.moodCode = moodCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Date getEffectiveTimeHigh() {
        return effectiveTimeHigh;
    }

    public void setEffectiveTimeHigh(Date effectiveTimeHigh) {
        this.effectiveTimeHigh = effectiveTimeHigh;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public ProductInstance getProductInstance() {
        return productInstance;
    }

    public void setProductInstance(ProductInstance productInstance) {
        this.productInstance = productInstance;
    }
}

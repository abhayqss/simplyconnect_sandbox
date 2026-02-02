package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "MedicalEquipment")
public class MedicalEquipment extends LegacyIdAwareEntity {
    @Column(length = 50, name = "mood_code")
    private String moodCode;

    @Column(length = 50, name = "status_code")
    private String statusCode;

    @Column(name = "effective_time_high")
    private Date effectiveTimeHigh;

    @Column
    private Integer quantity;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="product_instance_id")
    private ProductInstance productInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ProductInstance getProductInstance() {
        return productInstance;
    }

    public void setProductInstance(ProductInstance productInstance) {
        this.productInstance = productInstance;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }
}

package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "MarketplaceRating")
public class MarketplaceRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "federal_provider_number")
    private String federalProviderNumber;

    @Column(name = "provider_name")
    private String providerName;

    @Column(name = "overall_rating")
    private Integer overallRating;

    @Column(name = "processing_date", columnDefinition = "datetime2")
    private LocalDate processingDate;

    @Column(name = "is_manual")
    private boolean isManual;

    public MarketplaceRating() {
    }

    public MarketplaceRating(String federalProviderNumber, String providerName, Integer overallRating, LocalDate processingDate, boolean isManual) {
        this.federalProviderNumber = federalProviderNumber;
        this.providerName = providerName;
        this.overallRating = overallRating;
        this.processingDate = processingDate;
        this.isManual = isManual;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFederalProviderNumber() {
        return federalProviderNumber;
    }

    public void setFederalProviderNumber(String federalProviderNumber) {
        this.federalProviderNumber = federalProviderNumber;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public Integer getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Integer overallRating) {
        this.overallRating = overallRating;
    }

    public LocalDate getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(LocalDate processingDate) {
        this.processingDate = processingDate;
    }

    public boolean getIsManual() {
        return isManual;
    }

    public void setIsManual(boolean manual) {
        isManual = manual;
    }
}

package com.scnsoft.eldermark.exchange.fk;

public class MedicationForeignKeys implements ResidentIdAware {
    private Long manufacturerId;
    private Long authorId;
    private Long residentId;
    private Long providerId;
    private Long deliveryMethodCodeId;
    private Long routeCodeId;
    private Long productNameCodeId;
    private Long dispensingPharmacyId;
    private Long medicalProfessionalId;
    private Long pharmacyId;

    public Long getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(Long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public Long getDeliveryMethodCodeId() {
        return deliveryMethodCodeId;
    }

    public void setDeliveryMethodCodeId(Long deliveryMethodCodeId) {
        this.deliveryMethodCodeId = deliveryMethodCodeId;
    }

    public Long getRouteCodeId() {
        return routeCodeId;
    }

    public void setRouteCodeId(Long routeCodeId) {
        this.routeCodeId = routeCodeId;
    }

    public Long getProductNameCodeId() {
        return productNameCodeId;
    }

    public void setProductNameCodeId(Long productNameCodeId) {
        this.productNameCodeId = productNameCodeId;
    }

    public Long getDispensingPharmacyId() {
        return dispensingPharmacyId;
    }

    public void setDispensingPharmacyId(Long dispensingPharmacyId) {
        this.dispensingPharmacyId = dispensingPharmacyId;
    }

    public Long getMedicalProfessionalId() {
        return medicalProfessionalId;
    }

    public void setMedicalProfessionalId(Long medicalProfessionalId) {
        this.medicalProfessionalId = medicalProfessionalId;
    }

    public Long getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(Long pharmacyId) {
        this.pharmacyId = pharmacyId;
    }
}

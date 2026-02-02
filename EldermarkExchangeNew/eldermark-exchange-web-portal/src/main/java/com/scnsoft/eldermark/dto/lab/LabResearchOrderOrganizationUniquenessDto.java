package com.scnsoft.eldermark.dto.lab;

public class LabResearchOrderOrganizationUniquenessDto {
    private boolean requisitionNumber;

    public LabResearchOrderOrganizationUniquenessDto(boolean requisitionNumber) {
        this.requisitionNumber = requisitionNumber;
    }

    public boolean isRequisitionNumber() {
        return requisitionNumber;
    }

    public void setRequisitionNumber(boolean requisitionNumber) {
        this.requisitionNumber = requisitionNumber;
    }
}

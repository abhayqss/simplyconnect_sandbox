package com.scnsoft.eldermark.exchange.fk;

public class ProcedureForeignKeys {
    private Long procedureTypeId;
    private Long residentId;

    public Long getProcedureTypeId() {
        return procedureTypeId;
    }

    public void setProcedureTypeId(Long procedureTypeId) {
        this.procedureTypeId = procedureTypeId;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }
}

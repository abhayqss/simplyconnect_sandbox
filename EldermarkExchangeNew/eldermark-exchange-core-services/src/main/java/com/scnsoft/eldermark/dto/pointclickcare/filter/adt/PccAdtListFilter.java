package com.scnsoft.eldermark.dto.pointclickcare.filter.adt;

import com.scnsoft.eldermark.dto.pointclickcare.filter.PccGetParamsFilter;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.List;

public class PccAdtListFilter extends PccGetParamsFilter {
    private List<Long> adtRecordIds;
    private Long facId;
    private Long patientId;
    private LocalDate effectiveDate;
    private PccAdtRecordType recordType;

    public List<Long> getAdtRecordIds() {
        return adtRecordIds;
    }

    public void setAdtRecordIds(List<Long> adtRecordIds) {
        this.adtRecordIds = adtRecordIds;
    }

    public Long getFacId() {
        return facId;
    }

    public void setFacId(Long facId) {
        this.facId = facId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public PccAdtRecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(PccAdtRecordType recordType) {
        this.recordType = recordType;
    }

    @Override
    protected void fillParams(MultiValueMap<String, String> map) {
        addComaSeparated(map, "adtRecordIds", adtRecordIds);
        addNonNull(map, "facId", facId);
        addNonNull(map, "patientId", patientId);
        addNonNull(map, "effectiveDate", effectiveDate);
        if (recordType != null) {
            map.add("recordType", recordType.name().toLowerCase());
        }
    }
}

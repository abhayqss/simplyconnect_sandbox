package com.scnsoft.eldermark.dto.pointclickcare.filter.patient;

import com.scnsoft.eldermark.dto.pointclickcare.filter.PccGetParamsFilter;
import org.springframework.util.MultiValueMap;

import java.util.List;

public class PCCPatientListFilter extends PccGetParamsFilter {

    private Long facId;
    private List<String> patientStatus;

    public PCCPatientListFilter(Long facId) {
        this.facId = facId;
    }

    public Long getFacId() {
        return facId;
    }

    public void setFacId(Long facId) {
        this.facId = facId;
    }

    public List<String> getPatientStatus() {
        return patientStatus;
    }

    public void setPatientStatus(List<String> patientStatus) {
        this.patientStatus = patientStatus;
    }

    @Override
    protected void fillParams(MultiValueMap<String, String> map) {
        addNonNull(map, "facId", facId);
        addComaSeparated(map, "patientStatus", patientStatus);
    }
}

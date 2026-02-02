package com.scnsoft.eldermark.dto.pointclickcare.model.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scnsoft.eldermark.dto.pointclickcare.model.PccPagedResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PccPatientList extends PccPagedResponse<PCCPatient> {

}

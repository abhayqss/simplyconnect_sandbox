package com.scnsoft.eldermark.consana.sync.server.model.fhir;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class XMedicationActionPlanWrapper {

    private String resourceType;

    @JsonProperty("entry")
    private List<XMedicationActionPlan> plans;
}

package com.scnsoft.eldermark.consana.sync.server.model.fhir;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = XMedicationActionPlanDeserializer.class)
public class XMedicationActionPlan {

    private String id;

    private String status;
}

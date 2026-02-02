package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicationActionPlanData {
    private final String title;
    private final String originalFileName;
    private final String mimeType;
    private final byte[] data;
}

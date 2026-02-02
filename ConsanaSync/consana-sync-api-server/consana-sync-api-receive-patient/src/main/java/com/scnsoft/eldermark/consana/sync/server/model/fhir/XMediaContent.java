package com.scnsoft.eldermark.consana.sync.server.model.fhir;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class XMediaContent {

    private String contentType;

    private byte[] data;

    private String title;
}

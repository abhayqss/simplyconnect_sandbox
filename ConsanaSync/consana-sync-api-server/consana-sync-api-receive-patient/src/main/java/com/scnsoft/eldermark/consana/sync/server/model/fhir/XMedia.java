package com.scnsoft.eldermark.consana.sync.server.model.fhir;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class XMedia {

    private String resourceType;

    private String id;

    private String type;

    private XMediaContent content;
}

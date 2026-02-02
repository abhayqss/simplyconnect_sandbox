package com.scnsoft.eldermark.consana.sync.server.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class AllRelatedGroupDto {

    String rxcui;

    List<ConceptGroupDto> conceptGroup;

}

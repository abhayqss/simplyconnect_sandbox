package com.scnsoft.eldermark.consana.sync.server.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class RxNormResponseDto {

    private AllRelatedGroupDto allRelatedGroup;

}

package com.scnsoft.eldermark.consana.sync.server.common.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ReceiveConsanaPatientQueueDto {

    private String consanaXRefId;
    private String organizationId;
    private String communityId;
    private ConsanaPatientUpdateType updateType;

}

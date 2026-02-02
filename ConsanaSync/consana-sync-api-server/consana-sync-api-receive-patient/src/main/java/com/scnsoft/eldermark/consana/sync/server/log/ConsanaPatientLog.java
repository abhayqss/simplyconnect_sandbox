package com.scnsoft.eldermark.consana.sync.server.log;

import com.scnsoft.eldermark.consana.sync.server.common.model.dto.ConsanaPatientUpdateType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ConsanaPatientLog")
@Data
@NoArgsConstructor
public class ConsanaPatientLog extends ConsanaBaseLog {

    @Column(name = "consana_patient_id")
    private String consanaPatientId;

    @Column(name = "organization_id")
    private String organizationId;

    @Column(name = "community_id")
    private String communityOID;

    @Enumerated(EnumType.STRING)
    @Column(name = "api_update_type")
    private ConsanaPatientUpdateType apiUpdateType;

    public ConsanaPatientLog(String consanaPatientId,
                             String organizationId,
                             String communityOID,
                             ConsanaPatientUpdateType apiUpdateType) {
        this.consanaPatientId = consanaPatientId;
        this.organizationId = organizationId;
        this.communityOID = communityOID;
        this.apiUpdateType = apiUpdateType;
    }
}

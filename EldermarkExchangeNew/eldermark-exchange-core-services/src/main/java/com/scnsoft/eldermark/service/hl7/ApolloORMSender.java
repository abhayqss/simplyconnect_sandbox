package com.scnsoft.eldermark.service.hl7;

import com.scnsoft.eldermark.entity.lab.LabResearchOrderORM;

public interface ApolloORMSender {

    boolean send(LabResearchOrderORM orm);

}

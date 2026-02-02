package com.scnsoft.eldermark.service.hl7;

import com.scnsoft.eldermark.entity.lab.LabResearchOrderORM;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder;

public interface ApolloORMGenerator {

    LabResearchOrderORM generate(LabResearchOrder order);
}

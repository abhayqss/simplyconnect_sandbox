package com.scnsoft.eldermark.consana.sync.server.facade;

import com.scnsoft.eldermark.consana.sync.server.model.ConsanaSyncDto;

public interface ConsanaPatientUpdateFacade {

    void convertAndSendToQueue(ConsanaSyncDto consanaSyncDto);
}

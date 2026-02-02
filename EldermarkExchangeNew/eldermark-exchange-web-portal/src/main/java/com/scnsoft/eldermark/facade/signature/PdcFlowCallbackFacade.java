package com.scnsoft.eldermark.facade.signature;

import com.scnsoft.eldermark.dto.signature.pdcflow.PdcFlowCallbackDto;

public interface PdcFlowCallbackFacade {

    void processCallback(PdcFlowCallbackDto callbackDto, String auth);
}

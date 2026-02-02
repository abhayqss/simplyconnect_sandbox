package com.scnsoft.eldermark.service.rxnorm;

import com.scnsoft.eldermark.service.rxnorm.dto.NDCStatus;

public interface RxNormApiGateway {

    NDCStatus getNDCStatus(String nationalDrugCode);

    String getVersion();

}

package com.scnsoft.eldermark.service.rxnorm;

import com.scnsoft.eldermark.entity.document.CcdCode;

import java.util.Optional;

public interface NdcToRxnormResolver {

    Optional<CcdCode> resolve(String nationalDrugCode);

}

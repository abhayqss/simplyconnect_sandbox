package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.model.entity.CcdCode;
import com.scnsoft.eldermark.consana.sync.server.model.enums.CodeSystem;
import org.hl7.fhir.instance.model.Coding;

public interface CcdCodeService {

    CcdCode findOrCreate(String code, String displayName, String codeSystemOid, String codeSystemName);

    CcdCode findByCodeAndCodeSystem(String code, String codeSystem);

    CcdCode findByCodeAndValueSet(String code, String valueSet);

    CcdCode findOrCreate(Coding coding, CodeSystem codeSystem);
}
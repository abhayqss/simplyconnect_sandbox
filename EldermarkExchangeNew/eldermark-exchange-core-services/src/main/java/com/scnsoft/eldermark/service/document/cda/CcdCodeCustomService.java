package com.scnsoft.eldermark.service.document.cda;

import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;

import java.util.Optional;

public interface CcdCodeCustomService {

    //todo get rid of optional because value is never null

    Optional<CcdCode> findOrCreate(String code, String displayName, CodeSystem codeSystem);

    Optional<CcdCode> findOrCreate(String code, String displayName, CodeSystem codeSystem, ValueSetEnum valueSet);

    Optional<CcdCode> findOrCreate(String code, String displayName, String codeSystemOid, String codeSystemName);

    Optional<CcdCode> findOrCreate(String code, String displayName, String codeSystemOid, String codeSystemName,
                                   String valueSet);

}
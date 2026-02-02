package com.scnsoft.eldermark.services.cda;

import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.entity.CodeSystem;

public interface CcdCodeService {

    CcdCode findOrCreate(String code, String displayName, CodeSystem codeSystem);
    CcdCode findOrCreate(String code, String displayName, String codeSystemOid, String codeSystemName);
    CcdCode findOrCreate(String code, String displayName, String codeSystemOid, String codeSystemName, String valueSet);

}
package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.dao.CcdCodeDao;
import com.scnsoft.eldermark.consana.sync.server.dao.UnknownCcdCodeDao;
import com.scnsoft.eldermark.consana.sync.server.model.entity.CcdCode;
import com.scnsoft.eldermark.consana.sync.server.model.entity.UnknownCcdCode;
import com.scnsoft.eldermark.consana.sync.server.model.enums.CodeSystem;
import org.hl7.fhir.instance.model.Coding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(noRollbackFor = Exception.class)
public class CcdCodeServiceImpl implements CcdCodeService {

    @Autowired
    private CcdCodeDao ccdCodeDao;

    @Autowired
    private UnknownCcdCodeDao unknownCcdCodeDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CcdCode findOrCreate(String code, String displayName, String codeSystemOid, String codeSystemName) {
        var ccdCode = ccdCodeDao.getFirstByCodeAndCodeSystem(code, codeSystemOid);
        if (ccdCode != null) {
            return ccdCode;
        }
        var uCcdCode = createUnknownCcdCode(code, displayName, codeSystemOid, codeSystemName);
        return ccdCodeDao.getOne(uCcdCode.getId());
    }

    @Override
    public CcdCode findByCodeAndCodeSystem(String code, String codeSystem) {
        return ccdCodeDao.getFirstByCodeAndCodeSystem(code, codeSystem);
    }

    @Override
    public CcdCode findByCodeAndValueSet(String code, String valueSet) {
        return ccdCodeDao.getByCodeAndValueSet(code, valueSet);
    }

    @Override
    public CcdCode findOrCreate(Coding coding, CodeSystem codeSystem) {
        return findOrCreate(coding.getCode(), coding.getDisplay(), codeSystem.getOid(), codeSystem.getDisplayName());
    }

    private UnknownCcdCode createUnknownCcdCode(String code, String displayName, String codeSystemOid, String codeSystemName) {
        var uCcdCode = new UnknownCcdCode();
        uCcdCode.setCode(code);
        uCcdCode.setCodeSystem(codeSystemOid);
        uCcdCode.setCodeSystemName(codeSystemName);
        uCcdCode.setDisplayName(displayName);
        return unknownCcdCodeDao.save(uCcdCode);
    }
}

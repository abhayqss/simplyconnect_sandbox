package com.scnsoft.eldermark.services.cda;

import com.scnsoft.eldermark.dao.AnyCcdCodeDao;
import com.scnsoft.eldermark.dao.CcdCodeDao;
import com.scnsoft.eldermark.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author phomal
 * Created on 2/20/2018.
 */
@Service
public class CcdCodeServiceImpl implements CcdCodeService {
    private static final Logger logger = LoggerFactory.getLogger(CcdCodeServiceImpl.class);

    private final AnyCcdCodeDao anyCcdCodeDao;
    private final CcdCodeDao ccdCodeDao;

    @Autowired
    public CcdCodeServiceImpl(AnyCcdCodeDao anyCcdCodeDao, CcdCodeDao ccdCodeDao) {
        this.anyCcdCodeDao = anyCcdCodeDao;
        this.ccdCodeDao = ccdCodeDao;
    }

    @Override
    public CcdCode findOrCreate(String code, String displayName, CodeSystem codeSystem) {
        return findOrCreate(code, displayName, codeSystem.getOid(), codeSystem.getDisplayName());
    }

    @Override
    public CcdCode findOrCreate(String code, String displayName, String codeSystemOid, String codeSystemName) {
        return findOrCreate(code, displayName, codeSystemOid, codeSystemName, null);
    }

    @Override
    public CcdCode findOrCreate(String code, String displayName, String codeSystemOid, String codeSystemName, String valueSet) {
        AnyCcdCode anyCcdCode = anyCcdCodeDao.getCcdCode(code, codeSystemOid, displayName, valueSet);
        anyCcdCode = createIfNotFound(code, displayName, codeSystemOid, codeSystemName, anyCcdCode);

        return ccdCodeDao.find(anyCcdCode.getId());
    }

    private AnyCcdCode createIfNotFound(String code, String displayName, String codeSystemOid, String codeSystemName, AnyCcdCode anyCcdCode) {
        if (anyCcdCode == null) {
            logger.warn("Can't find CcdCode for code={}, codeSystem={}. The unknown code will be added to the database.", code, codeSystemOid);

            anyCcdCode = createUnknownCcdCode(code, displayName, codeSystemOid, codeSystemName);
        } else {
            if (isConcreteCcdCodeWithDifferentDisplayName(anyCcdCode, displayName)) {
                logger.warn("CcdCode for code={}, codeSystem={} has a different displayName (expected='{}', actual='{}'). " +
                        "The discrepant code will be saved as a separate entity.", code, codeSystemOid, anyCcdCode.getDisplayName(), displayName);

                anyCcdCode = createInterpretiveCcdCode((ConcreteCcdCode) anyCcdCode, displayName);
            }
        }
        return anyCcdCode;
    }

    private boolean isConcreteCcdCodeWithDifferentDisplayName(AnyCcdCode anyCcdCode, String displayName) {
        return anyCcdCode instanceof ConcreteCcdCode && displayName != null && !StringUtils.equalsIgnoreCase(anyCcdCode.getDisplayName(), displayName);
    }

    private UnknownCcdCode createUnknownCcdCode(String code, String displayName, String codeSystemOid, String codeSystemName) {
        final UnknownCcdCode uCcdCode = new UnknownCcdCode();
        uCcdCode.setCode(code);
        uCcdCode.setCodeSystem(codeSystemOid);
        uCcdCode.setCodeSystemName(codeSystemName);
        uCcdCode.setDisplayName(displayName);

        return anyCcdCodeDao.create(uCcdCode);
    }

    private InterpretiveCcdCode createInterpretiveCcdCode(ConcreteCcdCode anyCcdCode, String displayName) {
        final InterpretiveCcdCode iCcdCode = new InterpretiveCcdCode();
        iCcdCode.setReferredCcdCode(anyCcdCode);
        iCcdCode.setDisplayName(displayName);

        return anyCcdCodeDao.create(iCcdCode);
    }

}

package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.AnyCcdCode;
import com.scnsoft.eldermark.entity.ConcreteCcdCode;
import com.scnsoft.eldermark.entity.InterpretiveCcdCode;
import com.scnsoft.eldermark.entity.UnknownCcdCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author phomal
 * Created on 03/01/2017.
 */
@Repository
public class AnyCcdCodeDaoImpl implements AnyCcdCodeDao {

    @Autowired
    private ConcreteCcdCodeDao concreteCcdCodeDao;

    @Autowired
    private UnknownCcdCodeDao unknownCcdCodeDao;

    @Autowired
    private InterpretiveCcdCodeDao interpretiveCcdCodeDao;

    @Override
    public UnknownCcdCode create(UnknownCcdCode ccdCode) {
        return unknownCcdCodeDao.create(ccdCode);
    }

    @Override
    public InterpretiveCcdCode create(InterpretiveCcdCode ccdCode) {
        return interpretiveCcdCodeDao.create(ccdCode);
    }

    @Override
    public AnyCcdCode getCcdCode(String code, String codeSystem, String displayName) {
        return getCcdCode(code, codeSystem, displayName, null);
    }

    @Override
    public AnyCcdCode getCcdCode(String code, String codeSystem, String displayName, String valueSet) {
        final ConcreteCcdCode ccdCode = concreteCcdCodeDao.getCcdCode(code, codeSystem, valueSet);
        if (ccdCode == null) {
            List<UnknownCcdCode> unknownCcdCodes = unknownCcdCodeDao.getCcdCodes(code, codeSystem);
            for (UnknownCcdCode unknownCcdCode : unknownCcdCodes) {
                if (StringUtils.equals(unknownCcdCode.getDisplayName(), displayName)) {
                    return unknownCcdCode;
                }
            }

            return null;
        } else {
            if (displayName != null && !displayName.equals(ccdCode.getDisplayName())) {
                InterpretiveCcdCode iCcdCode = interpretiveCcdCodeDao.getCcdCode(ccdCode, displayName);
                if (iCcdCode != null) {
                    return iCcdCode;
                }
            }

            return ccdCode;
        }
    }

}

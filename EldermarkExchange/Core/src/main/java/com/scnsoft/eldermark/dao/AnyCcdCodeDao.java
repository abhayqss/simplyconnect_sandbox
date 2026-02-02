package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.AnyCcdCode;
import com.scnsoft.eldermark.entity.InterpretiveCcdCode;
import com.scnsoft.eldermark.entity.UnknownCcdCode;

/**
 * @author phomal
 * Created on 03/01/2017.
 */
public interface AnyCcdCodeDao {
    UnknownCcdCode create(UnknownCcdCode ccdCode);
    InterpretiveCcdCode create(InterpretiveCcdCode ccdCode);
    AnyCcdCode getCcdCode(String code, String codeSystem, String displayName);
    AnyCcdCode getCcdCode(String code, String codeSystem, String displayName, String valueSet);
}

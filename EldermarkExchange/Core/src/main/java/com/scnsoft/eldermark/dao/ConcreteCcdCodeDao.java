package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.ConcreteCcdCode;

/**
 * @author phomal
 * Created on 03/01/2017.
 */
public interface ConcreteCcdCodeDao {
    ConcreteCcdCode getCcdCode(String code, String codeSystem);
    ConcreteCcdCode getCcdCode(String code, String codeSystem, String valueSet);
}

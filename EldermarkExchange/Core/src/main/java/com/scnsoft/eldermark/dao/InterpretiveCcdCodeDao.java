package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.ConcreteCcdCode;
import com.scnsoft.eldermark.entity.InterpretiveCcdCode;

/**
 * @author phomal
 * Created on 03/01/2017.
 */
public interface InterpretiveCcdCodeDao extends BaseDao<InterpretiveCcdCode> {
    InterpretiveCcdCode getCcdCode(ConcreteCcdCode originalCode, String displayName);
}

package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.UnknownCcdCode;

import java.util.List;

/**
 * @author phomal
 * Created on 03/01/2017.
 */
public interface UnknownCcdCodeDao extends BaseDao<UnknownCcdCode> {
    List<UnknownCcdCode> getCcdCodes(String code, String codeSystem);
}

package com.scnsoft.eldermark.event.xml.dao;

import com.scnsoft.eldermark.dao.CcdCodeDao;
import com.scnsoft.eldermark.entity.document.CcdCode;

public interface EventCcdCodeDao extends CcdCodeDao {

    CcdCode getByCodeAndValueSetName(String code, String valueSetName);
}

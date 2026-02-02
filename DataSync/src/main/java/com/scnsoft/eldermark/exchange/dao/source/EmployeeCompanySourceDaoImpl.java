package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.EmployeeCompanyData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("employeeCompanySourceDao")
public class EmployeeCompanySourceDaoImpl extends StandardSourceDaoImpl<EmployeeCompanyData, Long> {
    public EmployeeCompanySourceDaoImpl() {
        super(EmployeeCompanyData.class, Long.class, Constants.SYNC_STATUS_COLUMN);
    }
}

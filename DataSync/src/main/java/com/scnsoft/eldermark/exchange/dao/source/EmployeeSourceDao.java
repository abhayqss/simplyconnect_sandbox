package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.model.source.EmployeeData;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.dao.source.SourceDao;
import com.scnsoft.eldermark.framework.dao.source.filters.SourceEntitiesFilter;

import java.util.List;

public interface EmployeeSourceDao extends SourceDao<String> {
    List<EmployeeData> getEmployees(Sql4DOperations sql4DOperations, SourceEntitiesFilter<String> employeesFilter,
                                    String password);

    boolean isPasswordValid(Sql4DOperations sql4DOperations, String password);
}

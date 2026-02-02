package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.model.source.SystemSetupData;
import com.scnsoft.eldermark.framework.connector4d.ColumnType4D;
import com.scnsoft.eldermark.framework.connector4d.ResultListParameters;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.dao.source.columnexpressions.ColumnExpression;
import com.scnsoft.eldermark.framework.dao.source.columnexpressions.SelectExpression;
import com.scnsoft.eldermark.framework.dao.source.operations.SourceEntityOperations;
import com.scnsoft.eldermark.framework.dao.source.operations.SourceEntityOperationsImpl;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Repository
public class SystemSetupSourceDaoImpl implements SystemSetupSourceDao {
    private static final List<SelectExpression> SELECT_EXPRESSIONS = Arrays.<SelectExpression>asList(
            new ColumnExpression(SystemSetupData.LOGIN_COMPANY_ID)
    );

    private static final ResultListParameters RESULT_LIST_PARAMETERS = new ResultListParameters(
            new ResultListParameters.Parameter(SystemSetupData.LOGIN_COMPANY_ID, ColumnType4D.STRING)
    );

    private final SourceEntityOperations<SystemSetupData> sourceEntityOperations =
            new SourceEntityOperationsImpl<SystemSetupData>(SystemSetupData.SYSTEM_SETUP_TABLE);

    @Override
    public SystemSetupData getSystemSetup(Sql4DOperations sqlOperations) {
        try {
            List<SystemSetupData> systemSetupList = sourceEntityOperations.getEntities(sqlOperations, SELECT_EXPRESSIONS, RESULT_LIST_PARAMETERS,
                    new RowMapper<SystemSetupData>() {
                        public SystemSetupData mapRow(ResultSet rs, int rowNum) throws SQLException {
                            SystemSetupData systemSetupRow = new SystemSetupData();
                            systemSetupRow.setLoginCompanyId(rs.getString(SystemSetupData.LOGIN_COMPANY_ID));
                            return systemSetupRow;
                        }
                    });

            if (systemSetupList.size() != 1) {
                throw new IllegalStateException("SystemSetup: Expected systemSetupList size is 1, but actual is " + systemSetupList.size());
            }
            return systemSetupList.get(0);
        } catch (DataAccessException e) {
            return null; // Catch of DataAccessException is to be removed after all 4D source databases would update schema with SystemSetup.Login_Company_Id
        }
    }
}

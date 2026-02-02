package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.EmployeeData;
import com.scnsoft.eldermark.framework.Utils;
import com.scnsoft.eldermark.framework.connector4d.ColumnType4D;
import com.scnsoft.eldermark.framework.connector4d.ResultListParameters;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.dao.source.columnexpressions.ColumnExpression;
import com.scnsoft.eldermark.framework.dao.source.columnexpressions.FunctionCallExpression;
import com.scnsoft.eldermark.framework.dao.source.columnexpressions.SelectExpression;
import com.scnsoft.eldermark.framework.dao.source.filters.MaxIdFilter;
import com.scnsoft.eldermark.framework.dao.source.filters.SourceEntitiesFilter;
import com.scnsoft.eldermark.framework.dao.source.operations.IdentifiableSourceEntityOperations;
import com.scnsoft.eldermark.framework.dao.source.operations.IdentifiableSourceEntityOperationsImpl;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EmployeeSourceDaoImpl implements EmployeeSourceDao {
    private static final String DECRYPTED_PASSWORD = "Decrypted_Password";

    private static final String PASSWORD_DECRYPTION_FAIL = "_fail";

    private IdentifiableSourceEntityOperations<EmployeeData, String> sourceEntityOperations =
            new IdentifiableSourceEntityOperationsImpl<EmployeeData, String>(EmployeeData.TABLE_NAME, EmployeeData.ID,
                    Constants.SYNC_STATUS_COLUMN, String.class);

    @Override
    public List<EmployeeData> getEmployees(Sql4DOperations sqlOperations, SourceEntitiesFilter<String> employeesFilter, String password) {
        Utils.ensureNotNull(password, "password");
        List<SelectExpression> selectExpressions = createSelectExpressions(password);
        ResultListParameters resultListParameters = createResultListParameters();
        return sourceEntityOperations.getEntities(sqlOperations, selectExpressions, resultListParameters, new EmployeeRowMapper(), employeesFilter);
    }

    @Override
    public boolean isPasswordValid(Sql4DOperations sqlOperations, String password) {
        Utils.ensureNotNull(sqlOperations, "jdbcOperations");
        Utils.ensureNotNull(password, "password");

        StringBuilder sb = new StringBuilder();
        //Not SQL-inject safe, but placeholder ? leads to ODBC native code crash
        sb.append("SELECT ").append(buildEmployeePasswordDecryptingExpression(password))
                .append(" as Decrypted_Password").append(" FROM " + EmployeeData.TABLE_NAME + " limit 1");
        List<String> employeePasswords = sqlOperations.queryForList(sb.toString(), String.class);

        boolean isPasswordCorrect;
        if (employeePasswords.isEmpty()) {
            //Assume that password is correct if no employees have been found
            isPasswordCorrect = true;
        } else {
            String employeePassword = employeePasswords.get(0);
            isPasswordCorrect = !PASSWORD_DECRYPTION_FAIL.equals(employeePassword);
        }
        return isPasswordCorrect;
    }

    @Override
    public String getMaxId(Sql4DOperations sqlOperations, MaxIdFilter<String> filter) {
        return sourceEntityOperations.getMaxId(sqlOperations, filter);
    }

    private String buildEmployeePasswordDecryptingExpression(String password) {
        return "({fn EmployeePassword_Show('" + password + "', employee.id) as TEXT})";
    }

    private List<SelectExpression> createSelectExpressions(String password) {
        StringBuilder sb = new StringBuilder();

        String passwordExpression = sb.append(buildEmployeePasswordDecryptingExpression(password))
                .append(" as ").append(DECRYPTED_PASSWORD).toString();

        List<SelectExpression> selectExpressions = new ArrayList<SelectExpression>();
        selectExpressions.add(new ColumnExpression(EmployeeData.ID));
        selectExpressions.add(new ColumnExpression(EmployeeData.FIRST_NAME));
        selectExpressions.add(new ColumnExpression(EmployeeData.LAST_NAME));
        selectExpressions.add(new ColumnExpression(EmployeeData.LOGIN_NAME));
        selectExpressions.add(new ColumnExpression(EmployeeData.INACTIVE));
        selectExpressions.add(new ColumnExpression(EmployeeData.SEC_GROUP_IDS));
        selectExpressions.add(new ColumnExpression(EmployeeData.EMAIL));
        selectExpressions.add(new ColumnExpression(EmployeeData.ADDRESS));
        selectExpressions.add(new ColumnExpression(EmployeeData.CITY));
        selectExpressions.add(new ColumnExpression(EmployeeData.STATE));
        selectExpressions.add(new ColumnExpression(EmployeeData.ZIP));
        selectExpressions.add(new ColumnExpression(EmployeeData.HOME_PHONE));
        selectExpressions.add(new ColumnExpression(EmployeeData.LASTMOD_STAMP));
        selectExpressions.add(new FunctionCallExpression(passwordExpression));
        return selectExpressions;
    }

    private ResultListParameters createResultListParameters() {
        ResultListParameters resultListParameters = new ResultListParameters();
        resultListParameters.addParameter(EmployeeData.ID, ColumnType4D.STRING );
        resultListParameters.addParameter(EmployeeData.FIRST_NAME, ColumnType4D.STRING );
        resultListParameters.addParameter(EmployeeData.LAST_NAME, ColumnType4D.STRING );
        resultListParameters.addParameter(EmployeeData.LOGIN_NAME, ColumnType4D.STRING );
        resultListParameters.addParameter(EmployeeData.INACTIVE, ColumnType4D.BOOLEAN);
        resultListParameters.addParameter(EmployeeData.SEC_GROUP_IDS, ColumnType4D.STRING );
        resultListParameters.addParameter(EmployeeData.EMAIL, ColumnType4D.STRING );
        resultListParameters.addParameter(EmployeeData.ADDRESS, ColumnType4D.STRING );
        resultListParameters.addParameter(EmployeeData.CITY, ColumnType4D.STRING );
        resultListParameters.addParameter(EmployeeData.STATE, ColumnType4D.STRING );
        resultListParameters.addParameter(EmployeeData.ZIP, ColumnType4D.STRING );
        resultListParameters.addParameter(EmployeeData.HOME_PHONE, ColumnType4D.STRING );
        resultListParameters.addParameter(EmployeeData.LASTMOD_STAMP, ColumnType4D.LONG );
        resultListParameters.addParameter(DECRYPTED_PASSWORD, ColumnType4D.STRING );
        return resultListParameters;
    }

    private static class EmployeeRowMapper implements RowMapper<EmployeeData> {
        @Override
        public EmployeeData mapRow(ResultSet rs, int rowNum) throws SQLException {
            EmployeeData employee = new EmployeeData();
            employee.setId(rs.getString(EmployeeData.ID));
            employee.setFirstName(rs.getString(EmployeeData.FIRST_NAME));
            employee.setLastName(rs.getString(EmployeeData.LAST_NAME));
            employee.setLoginName(rs.getString(EmployeeData.LOGIN_NAME));
            employee.setInactive(rs.getBoolean(EmployeeData.INACTIVE));
            employee.setPassword(rs.getString(DECRYPTED_PASSWORD));
            employee.setSecGroupIds(rs.getString(EmployeeData.SEC_GROUP_IDS));
            employee.setEmail(rs.getString(EmployeeData.EMAIL));
            employee.setAddress(rs.getString(EmployeeData.ADDRESS));
            employee.setCity(rs.getString(EmployeeData.CITY));
            employee.setState(rs.getString(EmployeeData.STATE));
            employee.setZip(rs.getString(EmployeeData.ZIP));
            employee.setHomePhone(rs.getString(EmployeeData.HOME_PHONE));
            employee.setLastmodStamp(rs.getLong(EmployeeData.LASTMOD_STAMP));
            return employee;
        }
    }

}

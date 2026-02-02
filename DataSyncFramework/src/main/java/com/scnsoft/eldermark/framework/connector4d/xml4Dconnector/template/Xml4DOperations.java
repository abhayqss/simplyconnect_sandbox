package com.scnsoft.eldermark.framework.connector4d.xml4Dconnector.template;


import com.scnsoft.eldermark.framework.Pair;
import com.scnsoft.eldermark.framework.connector4d.ColumnType4D;
import com.scnsoft.eldermark.framework.connector4d.ResultListParameters;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.connector4d.xml4Dconnector.schema.CommunicationRequest;
import com.scnsoft.eldermark.framework.connector4d.xml4Dconnector.schema.CommunicationResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by averazub on 5/24/2016.
 */
public class Xml4DOperations implements Sql4DOperations {

    private final String SINGLE_COLUMN_NAME = "column1";
    
    private Xml4DConnectionFactory connectionFactory;



    public Xml4DOperations(Xml4DConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    private Xml4DResultSet query(String sql, ResultListParameters resultListParameters, boolean isSingleResult) {

        Xml4DConnection connection = connectionFactory.createConnection();
        Xml4DResultSet resultSet = connection.query(sql, resultListParameters, isSingleResult);


        return resultSet;

    }

    public Xml4DResultSet queryForList(String sql, ResultListParameters resultListParameters) {
        return query(sql, resultListParameters, false);
    }

    public Xml4DResultSet queryForSingleResult(String sql, ResultListParameters resultListParameters) {
        return query(sql, resultListParameters, true);
    }



    public <T> List<T> query(String sql, ResultListParameters resultListParameters, RowMapper<T> rowMapper) throws DataAccessException {
        Xml4DResultSet resultSet = queryForList(sql, resultListParameters);
        List<T> result = new ArrayList<T>();
        int rowNum = 0;
        try {
            while (resultSet.next()) {
                result.add(rowMapper.mapRow(resultSet, rowNum++));
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), e.getCause()) {}; //TODO
        }
        return result;
    }

    public <T> T queryForSingleResult(String sql, ResultListParameters resultListParameters, RowMapper<T> rowMapper) throws SQLException{
        Xml4DResultSet resultSet = queryForSingleResult(sql, resultListParameters);
        if (resultSet.next()) {
            return rowMapper.mapRow(resultSet, 0);
        } else {
            return null;
        }
    }


    public <T> List<T> queryForList(String sql, Class<T> elementType) throws DataAccessException {
        ResultListParameters parameters = new ResultListParameters();
        parameters.addParameter(SINGLE_COLUMN_NAME, ColumnType4D.getTypeForClass(elementType));
        return queryForList(sql, parameters, elementType);
    }


    public <T> List<T> queryForList(String sql, ResultListParameters resultListParameters,  Class<T> clazz) throws DataAccessException{
        Xml4DResultSet resultSet = queryForList(sql, resultListParameters);
        List<T> result = new ArrayList<T>();
        try {
            while (resultSet.next()) {
                result.add(resultSet.getObject(1, clazz));
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), e.getCause()) {}; //TODO
        }
        return result;
    }

    public <T> T queryForSingleResult(String sql, ResultListParameters resultListParameters, Class<T> clazz) throws SQLException{
        Xml4DResultSet resultSet = queryForSingleResult(sql, resultListParameters);
        if (resultSet.next()) {
            return resultSet.getObject(1,clazz);
        } else {
            return null;
        }
    }


}

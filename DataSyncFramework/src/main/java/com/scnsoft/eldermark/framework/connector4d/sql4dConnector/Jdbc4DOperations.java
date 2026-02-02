package com.scnsoft.eldermark.framework.connector4d.sql4dConnector;

import com.scnsoft.eldermark.framework.connector4d.ResultListParameters;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

/**
 * Created by averazub on 5/25/2016.
 */
public class Jdbc4DOperations implements Sql4DOperations {

    JdbcOperations jdbcOperations;

    public Jdbc4DOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public <T> List<T> query(String sql, ResultListParameters resultListParameters, RowMapper<T> rowMapper) throws DataAccessException {
        return jdbcOperations.query(sql, rowMapper);
    }

    public <T> List<T> queryForList(String sql, Class<T> elementType) throws DataAccessException {
        return jdbcOperations.queryForList(sql, elementType);
    }
}

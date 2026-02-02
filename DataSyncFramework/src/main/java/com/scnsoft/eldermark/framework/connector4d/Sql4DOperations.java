package com.scnsoft.eldermark.framework.connector4d;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

/**
 * Created by averazub on 5/25/2016.
 */
public interface Sql4DOperations {
    <T> List<T> query(String sql, ResultListParameters resultListParameters, RowMapper<T> rowMapper) throws DataAccessException;
    <T> List<T> queryForList(String sql, Class<T> elementType) throws DataAccessException;

}

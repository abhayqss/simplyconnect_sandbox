package com.scnsoft.exchange.audit.dao;

import com.scnsoft.exchange.audit.model.StateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StateDaoImpl extends BaseDao implements StateDao {

    @Override
    public List<StateDto> findAll() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT  [OrganizationAddress].[state] as 'state_name' ");
        sql.append("FROM [Organization]");
        sql.append("	LEFT JOIN [OrganizationAddress] on [OrganizationAddress].[org_id] = [Organization].[id] ");
        sql.append("WHERE ");
        sql.append("   ([Organization].[legacy_table] = 'Company' OR [Organization].[legacy_table] IS NULL) ");
        sql.append("   AND ([Organization].[testing_training] = 0 OR [Organization].[testing_training] IS NULL) ");
        sql.append("ORDER BY state_name ASC ");

        return getJdbcTemplate().query(sql.toString(), new RowMapper<StateDto>() {
            @Override
            public StateDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                StateDto state = new StateDto();

                String name = rs.getString("state_name");
                state.setName(name);

                return state;
            }
        });
    }
}

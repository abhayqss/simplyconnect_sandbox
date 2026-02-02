package com.scnsoft.exchange.audit.dao;

import com.scnsoft.exchange.audit.model.CompanyDto;
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
public class CompanyDaoImpl extends BaseDao implements CompanyDao {

    @Override
    public List<CompanyDto> findAll() {
        String sql= "SELECT " +
                    "    id, name " +
                    "FROM SourceDatabase " +
                    "ORDER BY name";

        return getJdbcTemplate().query(sql, new RowMapper<CompanyDto>() {
            @Override
            public CompanyDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                CompanyDto company = new CompanyDto();

                company.setId(rs.getLong("id"));
                company.setName(rs.getString("name"));

                return company;
            }
        });
    }
}

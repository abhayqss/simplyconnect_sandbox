package com.scnsoft.exchange.audit.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

public abstract class BaseDao extends JdbcDaoSupport {

    @Autowired
    protected DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }
}

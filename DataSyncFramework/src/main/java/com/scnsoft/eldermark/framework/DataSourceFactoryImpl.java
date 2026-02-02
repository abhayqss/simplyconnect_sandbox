package com.scnsoft.eldermark.framework;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.stereotype.Component;

@Component
public class DataSourceFactoryImpl implements DataSourceFactory {
    private static final String JDBC_ODBC_DRIVER = "sun.jdbc.odbc.JdbcOdbcDriver";

    @Override
    public BasicDataSource createDatasource(DatabaseInfo database) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(database.getUrl());
        dataSource.setDriverClassName(JDBC_ODBC_DRIVER);
        dataSource.setInitialSize(1);

        //TODO configure basic datasource if necessary
        return dataSource;
    }
}

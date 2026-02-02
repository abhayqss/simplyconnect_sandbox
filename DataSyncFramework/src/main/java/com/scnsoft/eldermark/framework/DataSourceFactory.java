package com.scnsoft.eldermark.framework;

import org.apache.commons.dbcp.BasicDataSource;

public interface DataSourceFactory {
    BasicDataSource createDatasource(DatabaseInfo database);
}

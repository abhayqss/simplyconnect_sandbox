package com.scnsoft.eldermark.dao.dialect;

import org.hibernate.dialect.SQLServer2008Dialect;

/**
 * Created by averazub on 10/28/2016.
 */
public class SqlServerCustomDialect extends SQLServer2008Dialect {

    public static final int MSSQL_WHERE_IN_PARAM_LIMIT = 2000;

    @Override
    public String appendIdentitySelectToInsert(String insertSQL) {
        return insertSQL + " select @@IDENTITY";
    }

}

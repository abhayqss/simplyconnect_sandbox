package com.scnsoft.eldermark.dao.dialect;

import org.hibernate.dialect.SQLServerDialect;

/**
 * Created by averazub on 10/28/2016.
 */
public class SqlServerCustomDialect extends SQLServerDialect {

    public SqlServerCustomDialect() {
        super();
    }

    @Override
    public String appendIdentitySelectToInsert(String insertSQL) {
        return insertSQL + " select @@IDENTITY";
    }



}

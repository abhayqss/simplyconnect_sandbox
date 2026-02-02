package com.scnsoft.eldermark.exchange;

import com.scnsoft.eldermark.framework.DataSourceFactory;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.*;

/**
 * Created by averazub on 6/1/2016.
 */
public class Main2 {
    public static void main(String[] args) throws Exception {


        ApplicationContext ctx;
        ctx = new ClassPathXmlApplicationContext("/spring/exchangeDataSyncContext.xml");
        JdbcOperations jdbcOperations = (JdbcOperations) ctx.getBean("targetDatabaseJdbcTemplate");
        JdbcOperations jdbcOperations2 = (JdbcOperations) ctx.getBean("targetDatabaseJdbcTemplate2");
        class ColumnCount {
            Integer columnCount = null;
        }
        final ColumnCount cc = new ColumnCount();
        String tableName = "dbo.[resident]";


        DataSource ds = (DataSource) ctx.getBean("targetDatabaseDataSource");
        DataSource ds2 =(DataSource)  ctx.getBean("targetDatabaseDataSource2");
        Connection c1 = ds.getConnection();
        Connection c2 = ds2.getConnection();
        ResultSet rs1 = c1.prepareStatement("SELECT TOP 1000 * FROM " + tableName + " order by database_id desc, legacy_id").executeQuery();
        if (cc.columnCount==null) cc.columnCount = rs1.getMetaData().getColumnCount();
        ResultSet rs2 = c2.prepareStatement("SELECT TOP 1000 * FROM " + tableName + " order by database_id desc, legacy_id").executeQuery();

        while (rs1.next()) {
            if (!rs2.next()) {
                System.out.println("Different number of rows in table "+tableName);
                return;
            }
            for (int i=1;i<=cc.columnCount;i++) {
                if (rs1.getMetaData().getColumnName(i).endsWith("id")) continue;
                String legacys = "("+rs1.getString("legacy_id")+","+rs2.getString("legacy_id")+","+rs1.getMetaData().getColumnName(i)+")";

                if ((rs1.getObject(i)!=null) && (rs2.getObject(i)!=null)) {
                    if (!rs1.getObject(i).equals(rs2.getObject(i))) System.out.println(legacys+"Different values " + rs1.getMetaData().getColumnName(i) + "," + rs1.getMetaData().getColumnName(i)+ ",: "+ rs1.getObject(i)+" " +rs2.getObject(i));
                    //Both not null
                } else if (rs1.getObject(i)!=null) {
                    System.out.println(legacys+"RS2 IS NULL, But rs1 = "+ rs1.getObject(i));
                    //RS1 not null, RS2 null
                } else if (rs1.getObject(i)!=null) {
                    System.out.println(legacys+"RS1 IS NULL, But rs2 = "+ rs2.getObject(i));
                    //RS2 not null, RS1 null
                } else {
                    //BOTH NULL
                }
            }
        }

    }


}

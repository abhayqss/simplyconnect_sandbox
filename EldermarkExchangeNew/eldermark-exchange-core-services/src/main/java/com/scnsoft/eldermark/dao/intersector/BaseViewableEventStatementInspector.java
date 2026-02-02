package com.scnsoft.eldermark.dao.intersector;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public abstract class BaseViewableEventStatementInspector implements StatementInspector, MetadataBuilderContributor {
    private static final Logger logger = LoggerFactory.getLogger(BaseViewableEventStatementInspector.class);

    private final String viewableFunctionName;
    private final String fakeFunctionName;

    protected BaseViewableEventStatementInspector(String notViewableFunctionName, String fakeFunctionName) {
        this.viewableFunctionName = notViewableFunctionName;
        this.fakeFunctionName = fakeFunctionName;
    }

    @Override
    public void contribute(MetadataBuilder metadataBuilder) {
        metadataBuilder.applySqlFunction(
                fakeFunctionName,
                new StandardSQLFunction(
                        fakeFunctionName,
                        StandardBasicTypes.BOOLEAN
                )
        );
    }

    @Override
    public String inspect(String sql) {
        if (!sql.contains(fakeFunctionName)) {
            return sql;
        }
        logger.debug("Incoming SQL request with {} function", fakeFunctionName);

        var startIdx = 0;

        int functionStartIdx;
        while ((functionStartIdx = sql.indexOf(fakeFunctionName, startIdx)) != -1) {
            var functionEndIdx = functionStartIdx + fakeFunctionName.length();
            var argsStart = sql.indexOf("(", functionEndIdx);
            var argsEnd = -1;

            String[] args = new String[getFakeFunctionArgsCount()];
            var argsIdx = 0;

            var openBracketsCount = 0;
            var argStartIdx = argsStart + 1;
            for (int i = argsStart + 1; i < sql.length(); ++i) {
                var c = sql.charAt(i);
                switch (c) {
                    case '(':
                        openBracketsCount++;
                        break;
                    case ',':
                        if (openBracketsCount == 0) {
                            args[argsIdx] = sql.substring(argStartIdx, i);
                            argStartIdx = i + 1;
                            argsIdx++;
                        }
                        break;
                    case ')':
                        if (openBracketsCount > 0) {
                            openBracketsCount--;
                        } else {
                            args[argsIdx] = sql.substring(argStartIdx, i);
                            argsEnd = i;
                        }
                        break;
                }

                if (argsEnd != -1) {
                    //all args parsed
                    break;
                }
            }

            if (argsEnd == -1 || Stream.of(args).anyMatch(StringUtils::isBlank)) {
                throw new RuntimeException("Failed to parse query for " + fakeFunctionName + " function: " + sql);
            }

            var newSql = sql.substring(0, functionStartIdx) +
                    "(select IIF(count(distinct func_call.employee_id) = " + getEmployeesCount(args) + ", 1, 0) from "
                    + viewableFunctionName + "("
                    + getEmployee(args)
                    + "," + getResidentId(args)
                    + "," + getEventTypeId(args)
                    + ") func_call where func_call.can_view = 0"
                    + sql.substring(argsEnd);
            sql = newSql;
        }

        return sql;
    }

    protected abstract int getFakeFunctionArgsCount();

    protected String getEmployee(String[] args) {
        return args[0];
    }

    protected String getResidentId(String[] args) {
        return args[1];
    }

    protected String getEventTypeId(String[] args) {
        return args[2];
    }

    protected abstract String getEmployeesCount(String[] args);
}

package com.scnsoft.eldermark.dao.intersector;

import com.scnsoft.eldermark.exception.ApplicationException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.Order;
import java.util.ArrayList;
import java.util.List;

public class RowNumberStatementInspector implements StatementInspector, MetadataBuilderContributor {

    public static final String ROW_NUMBER_FAKE_FUNCTION = "ROW_NUMBER_FAKE";
    private static final Logger logger = LoggerFactory.getLogger(RowNumberStatementInspector.class);
    private static final String ITEM_ALIAS = "__item_id__";
    private static final String ROW_ALIAS = "__row_number__";


    @Override
    public void contribute(MetadataBuilder metadataBuilder) {
        //create separate MetadataBuilderContributor if more functions need to be registered
        metadataBuilder.applySqlFunction(
                ROW_NUMBER_FAKE_FUNCTION,
                new StandardSQLFunction(
                        ROW_NUMBER_FAKE_FUNCTION,
                        StandardBasicTypes.LONG
                )
        );
    }

    @Override
    public String inspect(String sql) {
        if (!sql.contains(ROW_NUMBER_FAKE_FUNCTION)) {
            return sql;
        }
        logger.debug("Incoming SQL request with {} function", ROW_NUMBER_FAKE_FUNCTION);

        var query = parseQuery(sql);
        var newQuery = buildNewQuery(query);

        logger.debug("new Query is \n{}", newQuery);
        return newQuery;
    }

    private RowNumberQuery parseQuery(String sql) {
        var query = new RowNumberQuery();

        splitSelect(query, sql);
        parseArgsAndAlias(query);

        return query;
    }

    private void splitSelect(RowNumberQuery query, String sql) {
        var selectIdx = sql.indexOf(" from ");
        var select = sql.substring(0, selectIdx);
        var restQuery = sql.substring(selectIdx + 1);

        query.setSelect(select);
        query.setRestQuery(restQuery);
    }

    private void parseArgsAndAlias(RowNumberQuery query) {
        var select = query.getSelect();

        var argsStart = select.indexOf(ROW_NUMBER_FAKE_FUNCTION) + ROW_NUMBER_FAKE_FUNCTION.length() + 1;
        var argsEnd = select.indexOf(')', argsStart);
        var argsStr = select.substring(argsStart, argsEnd);

        //looks like hibernate always adds alias but it potentially can be empty?
        var outputAlias = select.substring(argsEnd + 1).replaceAll("as", "").trim();

        var args = argsStr.split(",");

        if (args.length <= 2) {
            throw new ApplicationException("Order must be present for ROW_NUMBER() OVER(...) sql function");
        }

        var orders = new ArrayList<String>();
        for (int i = 2; i < args.length; i += 2) {
            orders.add(buildOrderStr(args[i].trim(), args[i + 1].trim()));
        }

        query.setItemSelectorField(args[0].trim());
        query.setItemSelectorValue(args[1].trim());
        query.setOrderBy(orders);
        query.setOutputAlias(outputAlias);
    }

    private String buildOrderStr(String field, String direction) {
        return field + " " + SortDirection.fromCode(Long.valueOf(direction)).name();
    }

    private String buildNewQuery(RowNumberQuery query) {
        var newSubQuerySelect = buildNewSubSelect(query);

        return "select " +
                ROW_ALIAS +
                " as " +
                query.getOutputAlias() +
                " from (" +
                newSubQuerySelect +
                ") listWithRowNumber where " +
                ITEM_ALIAS +
                " = " +
                query.getItemSelectorValue();
    }

    private String buildNewSubSelect(RowNumberQuery query) {
        var ordersStr = StringUtils.join(query.getOrderBy().toArray(), ", ");

        return "select " +
                query.getItemSelectorField() + " as " +
                ITEM_ALIAS +
                ", ROW_NUMBER() OVER(ORDER BY " +
                ordersStr +
                ") as " +
                ROW_ALIAS +
                " " + query.getRestQuery();
    }


    public enum SortDirection {
        DESC(0L),
        ASC(1L);

        private final long code;

        SortDirection(long code) {
            this.code = code;
        }


        public static SortDirection fromOrder(Order order) {
            return order.isAscending() ? ASC : DESC;
        }

        public static SortDirection fromCode(long code) {
            if (code == 0L) {
                return DESC;
            }
            if (code == 1L) {
                return ASC;
            }
            return ASC;
        }

        public long getCode() {
            return code;
        }
    }

    private static class RowNumberQuery {
        private String select;
        private String restQuery;

        private String itemSelectorField;
        private String itemSelectorValue;

        private List<String> orderBy;

        private String outputAlias;

        public String getSelect() {
            return select;
        }

        public void setSelect(String select) {
            this.select = select;
        }

        public String getRestQuery() {
            return restQuery;
        }

        public void setRestQuery(String restQuery) {
            this.restQuery = restQuery;
        }

        public String getItemSelectorField() {
            return itemSelectorField;
        }

        public void setItemSelectorField(String itemSelectorField) {
            this.itemSelectorField = itemSelectorField;
        }

        public String getItemSelectorValue() {
            return itemSelectorValue;
        }

        public void setItemSelectorValue(String itemSelectorValue) {
            this.itemSelectorValue = itemSelectorValue;
        }

        public List<String> getOrderBy() {
            return orderBy;
        }

        public void setOrderBy(List<String> orderBy) {
            this.orderBy = orderBy;
        }

        public String getOutputAlias() {
            return outputAlias;
        }

        public void setOutputAlias(String outputAlias) {
            this.outputAlias = outputAlias;
        }
    }
}

package com.scnsoft.eldermark.framework.dao.source;

import com.scnsoft.eldermark.framework.Utils;
import com.scnsoft.eldermark.framework.connector4d.ColumnType4D;
import com.scnsoft.eldermark.framework.connector4d.ResultListParameters;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.dao.source.columnexpressions.ColumnExpression;
import com.scnsoft.eldermark.framework.dao.source.columnexpressions.SelectExpression;
import com.scnsoft.eldermark.framework.dao.source.filters.MaxIdFilter;
import com.scnsoft.eldermark.framework.dao.source.filters.SourceEntitiesFilter;
import com.scnsoft.eldermark.framework.dao.source.operations.IdentifiableSourceEntityOperations;
import com.scnsoft.eldermark.framework.dao.source.operations.IdentifiableSourceEntityOperationsImpl;
import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;
import org.springframework.jdbc.core.RowMapper;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.*;

public class StandardSourceDaoImpl<E extends IdentifiableSourceEntity<ID>, ID extends Comparable<ID>>
        implements StandardSourceDao<E, ID> {

    private Class<E> entityClass;

    private final RowMapper<E> rowMapper;
    private final List<SelectExpression> selectExpressions;
    private final IdentifiableSourceEntityOperations<E, ID> sourceEntityOperations;
    private final ResultListParameters resultListParameters;

    protected StandardSourceDaoImpl(Class<E> entityClass, Class<ID> idClass, String syncStatusColumnName) {
        String tableName = getTableName(entityClass);
        String idColumnName = getIdColumnName(entityClass);
        Map<String, Field> columnsToFieldsMap = getColumnsToFieldsMap(entityClass);


        Utils.makeFieldsAccessible(columnsToFieldsMap.values());

        List<String> keyNames = new ArrayList<String>(columnsToFieldsMap.keySet());
        selectExpressions = createSelectExpressions(keyNames);
        resultListParameters = createResultListParameters(keyNames, columnsToFieldsMap);

        rowMapper = new AnnotatedEntityRowMapper(entityClass, columnsToFieldsMap);
        sourceEntityOperations = new IdentifiableSourceEntityOperationsImpl<E, ID>(tableName, idColumnName,
                syncStatusColumnName, idClass);


    }

    protected StandardSourceDaoImpl(Class<E> entityClass, IdentifiableSourceEntityOperationsImpl<E, ID> sourceEntityOperations) {
        Map<String, Field> columnsToFieldsMap = getColumnsToFieldsMap(entityClass);

        Utils.makeFieldsAccessible(columnsToFieldsMap.values());
        List<String> keyNames = new ArrayList<String>(columnsToFieldsMap.keySet());
        selectExpressions = createSelectExpressions(keyNames);
        resultListParameters = createResultListParameters(keyNames, columnsToFieldsMap);
        rowMapper = new AnnotatedEntityRowMapper(entityClass, columnsToFieldsMap);
        this.sourceEntityOperations = sourceEntityOperations;
    }

    @Override
    public List<E> getEntities(Sql4DOperations sql4DOperations, SourceEntitiesFilter<ID> filter) {
        try {
            return sourceEntityOperations.getEntities(sql4DOperations, selectExpressions, resultListParameters, rowMapper,  filter);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public ID getMaxId(Sql4DOperations sql4DOperations, MaxIdFilter<ID> filter) {
        return sourceEntityOperations.getMaxId(sql4DOperations, filter);
    }

    private List<SelectExpression> createSelectExpressions(Collection<String> columns) {
        List<SelectExpression> selectExpressions = new ArrayList<SelectExpression>();
        for (String column : columns) {
            selectExpressions.add(new ColumnExpression(column));
        }
        return selectExpressions;
    }

    protected ResultListParameters createResultListParameters(List<String> keyList, Map<String, Field> columnsToFieldsMap) {
        ResultListParameters resultListParameters = new ResultListParameters();
        for (String key: keyList) {
            Field column = columnsToFieldsMap.get(key);
            resultListParameters.addParameter(key, ColumnType4D.getTypeForClass(column.getType()));
        }
        return resultListParameters;
    }

    public Map<String, Field> getColumnsToFieldsMap(Class<E> entityClass) {
        Map<String, Field> resultMap = new HashMap<String, Field>();
        for (Field field : entityClass.getDeclaredFields()) {
            if (!Utils.isStaticField(field)) {
                String fieldColumnName = getColumnName(field);
                if (resultMap.containsKey(fieldColumnName)) {
                    throw new IllegalStateException(
                            "Multiple fields are mapped to the same column '" + fieldColumnName + "'");
                }
                resultMap.put(fieldColumnName, field);
            }
        }
        return resultMap;
    }

    private String getIdColumnName(Class<E> entityClass) {
        List<Field> idFields = new ArrayList<Field>();
        for (Field field : entityClass.getDeclaredFields()) {
            Id idAnnotation = field.getAnnotation(Id.class);
            if (!Utils.isStaticField(field) && idAnnotation != null) {
                idFields.add(field);
            }
        }

        if (idFields.size() != 1) {
            throw new IllegalStateException("Exactly one id annotation is required for class: "
                    + entityClass.getSimpleName());
        }

        return getColumnName(idFields.get(0));
    }

    private String getTableName(Class<E> entityClass) {
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        if (tableAnnotation == null) {
            throw new IllegalStateException("Class " + entityClass.getSimpleName() + " isn't mapped to a database table");
        }

        String tableName = tableAnnotation.value();
        if (Utils.isEmpty(tableName)) {
            throw new IllegalStateException("Class " + entityClass.getSimpleName() + ": table name is empty");
        }
        return tableName;
    }

    private String getColumnName(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);

        String fullFieldName = field.getDeclaringClass().getSimpleName() + "." + field.getName();
        if (columnAnnotation == null) {
            throw new IllegalStateException("Field '" + fullFieldName + "' isn't mapped to a database column");
        }

        String columnName = columnAnnotation.value();
        if (Utils.isEmpty(columnName)) {
            throw new IllegalStateException("Field '" + fullFieldName + " is mapped to an empty column name");
        }
        return columnName;
    }

    private class AnnotatedEntityRowMapper implements RowMapper<E> {
        private final Class<E> entityClass;
        private final Map<String, Field> columnsToFieldsMap;

        public AnnotatedEntityRowMapper(Class<E> entityClass, Map<String, Field> columnsToFieldsMap) {
            this.entityClass = entityClass;
            this.columnsToFieldsMap = columnsToFieldsMap;
        }

        @Override
        public E mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                E instance = entityClass.newInstance();

                for (Map.Entry<String, Field> entry : columnsToFieldsMap.entrySet()) {
                    Field field = entry.getValue();
                    Class<?> clazz = field.getType();
                    String columnName = entry.getKey();

                    Object columnValue;
                    if (Long.class.equals(clazz) || long.class.equals(clazz)) {
                        columnValue = rs.getLong(columnName);
                    } else if (Integer.class.equals(clazz) || int.class.equals(clazz)) {
                        columnValue = rs.getInt(columnName);
                    } else if (Boolean.class.equals(clazz) || boolean.class.equals(clazz)) {
                        columnValue = rs.getBoolean(columnName);
                    } else if (String.class.equals(clazz)) {
                        columnValue = rs.getString(columnName);
                    } else if (java.util.Date.class.equals(clazz) || java.sql.Date.class.equals(clazz)) {
                        columnValue = Utils.convertZeroDateToNull(rs, columnName);
                    } else if (Time.class.equals(clazz)) {
                        columnValue = Utils.convertZeroTimeToNull(rs, columnName);
                    } else if (BigDecimal.class.equals(clazz)) {
                        columnValue = rs.getBigDecimal(columnName);
                    } else {
                        throw new IllegalStateException("Unmapped field type:" + clazz.getName());
                    }
                    field.set(instance, columnValue);
                }
                return instance;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    private class LogEntityRowMapper implements RowMapper<E> {
        private final Class<E> entityClass;
        private final Map<String, Field> columnsToFieldsMap;

        public LogEntityRowMapper(Class<E> entityClass, Map<String, Field> columnsToFieldsMap) {
            this.entityClass = entityClass;
            this.columnsToFieldsMap = columnsToFieldsMap;
        }

        @Override
        public E mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                E instance = entityClass.newInstance();
                File f = null;
                try {
                    f = new File("d:\\report"+"-3-"+entityClass.getSimpleName()+".txt");
                    if (!f.exists()) f.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FileWriter fw = new FileWriter(f, true);
                fw.write("----------");
                for (Map.Entry<String, Field> entry : columnsToFieldsMap.entrySet()) {
                    Field field = entry.getValue();
                    Class<?> clazz = field.getType();
                    String columnName = entry.getKey();

                    Object columnValue;
                    if (Long.class.equals(clazz) || long.class.equals(clazz)) {
                        columnValue = rs.getLong(columnName);
                        fw.write("Class: "+clazz+",RowNum: "+rowNum+",LONG,"+(columnValue==null?"--null--":columnValue)+"\n");
                    } else if (Integer.class.equals(clazz) || int.class.equals(clazz)) {
                        columnValue = rs.getInt(columnName);
                        fw.write("Class: "+clazz+",RowNum: "+rowNum+",INT,"+(columnValue==null?"--null--":columnValue)+"\n");
                    } else if (Boolean.class.equals(clazz) || boolean.class.equals(clazz)) {
                        columnValue = rs.getBoolean(columnName);
                        fw.write("Class: "+clazz+",RowNum: "+rowNum+",BOOLEAN,"+(columnValue==null?"--null--":columnValue)+"\n");
                    } else if (String.class.equals(clazz)) {
                        columnValue = rs.getString(columnName);
                        fw.write("Class: " + clazz + ",RowNum: " + rowNum + ",STRING," + (columnValue == null ? "--null--" : columnValue) + ",length" + (columnValue == null ? 0 : columnValue.toString().length()) + "\n");
                    } else if (java.util.Date.class.equals(clazz) || java.sql.Date.class.equals(clazz)) {
                        columnValue = Utils.convertZeroDateToNull(rs, columnName);
                        fw.write("Class: "+clazz+",RowNum: "+rowNum+",DATE,"+((columnValue==null?"--null--":((Date)columnValue)).toString())+"\n");
                    } else if (Time.class.equals(clazz)) {
                        columnValue = Utils.convertZeroTimeToNull(rs, columnName);
                        fw.write("Class: "+clazz+",RowNum: "+rowNum+",TIME,"+((columnValue==null?"--null--":((Time)columnValue)).toString())+"\n");
                    } else if (BigDecimal.class.equals(clazz)) {
                        columnValue = rs.getBigDecimal(columnName);
                        fw.write("Class: "+clazz+",RowNum: "+rowNum+",BIGDECIMAL,"+(columnValue==null?"--null--":columnValue)+"\n");
                    } else {
                        throw new IllegalStateException("Unmapped field type:" + clazz.getName());
                    }
                    fw.write("Field: "+field.getName()+" , columnName: "+columnName);
                    field.set(instance, columnValue);

                }
                fw.write("------");
                fw.close();;
                return instance;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}

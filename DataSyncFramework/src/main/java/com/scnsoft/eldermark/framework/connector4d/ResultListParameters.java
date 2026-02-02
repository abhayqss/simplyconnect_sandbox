package com.scnsoft.eldermark.framework.connector4d;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by averazub on 3/11/2016.
 */
public class ResultListParameters {
    private Integer parameterCount = 0;
    private Map<Integer, Parameter> parameterMap = new HashMap<Integer, Parameter>();

    public static class Parameter {
        String columnName;
        ColumnType4D type;

        public Parameter(String columnName, ColumnType4D type) {
            this.columnName = columnName;
            this.type = type;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public ColumnType4D getType() {
            return type;
        }

        public void setType(ColumnType4D type) {
            this.type = type;
        }
    }


    public ResultListParameters() {

    }

    public ResultListParameters(Parameter... parameters) {
        for (Parameter parameter : parameters) {
            parameterMap.put(parameterCount, parameter);
            parameterCount++;
        }
    }

    public ResultListParameters addParameter(Parameter parameter) {

        parameterMap.put(parameterCount, parameter);
        parameterCount++;
        return this;
    }

    public ResultListParameters addParameter(String columnName, ColumnType4D type) {
        parameterMap.put(parameterCount, new Parameter(columnName, type));
        parameterCount++;
        return this;
    }

    public Integer getParameterCount() {
        return parameterCount;
    }


    public Map<Integer, Parameter> getParameterMap() {
        return parameterMap;
    }

}

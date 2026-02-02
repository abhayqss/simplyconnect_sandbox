package com.scnsoft.eldermark.framework.connector4d.xml4Dconnector.schema;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by averazub on 5/24/2016.
 */
public class Xml4DResultSetItem {
    private Map<Integer, String> valueStrByNumberMap;
    private Map<String, String> valueStrByColumnNameMap;

    public Xml4DResultSetItem() {
        this.valueStrByColumnNameMap = new HashMap<String, String>();
        this.valueStrByNumberMap = new HashMap<Integer, String>();
    }

    public Xml4DResultSetItem pushValue(int columnNumber, String columnName, String stringValue) {
        valueStrByNumberMap.put(columnNumber, stringValue);
        valueStrByColumnNameMap.put(columnName, stringValue);
        return this;
    }

    public String getValue(int columnNumber) {
        return valueStrByNumberMap.get(columnNumber);
    }

    public String getValue(String columnName) {
        return valueStrByColumnNameMap.get(columnName);
    }


}

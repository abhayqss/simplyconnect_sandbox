package com.scnsoft.eldermark.framework.connector4d;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import java.io.InputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by averazub on 3/11/2016.
 */
public enum ColumnType4D {

    STRING("text", String.class),
    LONG("32 bit integer", Long.class),
    INT("16 bit integer", Integer.class),
    REAL("numeric", Double.class),
    DATE("date", Date.class),
    TIME("time", Date.class),
    BOOLEAN("boolean", Boolean.class),
    PICTURE("picture", Byte[].class),
    BLOB("blob", Byte[].class);



    String singleTypeSource;
    String multipleTypeSource;
    Class destClass;

    ColumnType4D(String singleTypeSource, Class destClass) {
        this(singleTypeSource, singleTypeSource+" array", destClass);
    }

    ColumnType4D(String singleTypeSource, String multipleTypeSource, Class destClass) {
        this.singleTypeSource = singleTypeSource;
        this.multipleTypeSource = multipleTypeSource;
        this.destClass = destClass;
    }

    public static ColumnType4D getFromStringTypeSource(String typeSource) {
        for (ColumnType4D type: ColumnType4D.values()) {
            if ((typeSource.equals(type.getSingleTypeSource())) || (typeSource.equals(type.getMultipleTypeSource()))) {
                return type;
            }
        }
        return STRING;
    }

    public static ColumnType4D getTypeForClass(Class type) {
        if ((type.equals(Integer.class))||(type==int.class)) {
            return INT;
        } else if ((type.equals(Double.class))||(type==double.class)) {
            return REAL;
        } else if ((type.equals(Long.class))||(type==long.class)) {
            return LONG;
        } else if (type.equals(java.util.Date.class)) {
            return DATE;
        } else if (type.equals(java.sql.Date.class)) {
            return DATE;
        } else if ((type.equals(Boolean.class))||(type==boolean.class)) {
            return BOOLEAN;
        } else if (type.equals(InputStream.class)) {
            return BLOB;
        } else if ((type.equals(Byte.class))||(type==byte.class)) {
            return INT;
        } else if ((type.equals(Byte[].class))||(type==byte[].class)) {
            return BLOB;
        } else if (type.equals(Reader.class)) {
            return BLOB;
        } else if ((type.equals(Float.class))||(type==float.class)) {
            return REAL;
        } else if ((type.equals(Short.class))||(type==short.class)) {
            return INT;
        } else if (type.equals(String.class)) {
            return STRING;
        } else if (type.equals(Time.class)) {
            return INT;
        } else if (type.equals(Timestamp.class)) {
            return DATE;
        } else if (type.equals(BigDecimal.class)) {
            return REAL;
        }
        throw new RuntimeException("Unsupported type "+type.getName());

    }

    public String getSingleTypeSource() {
        return singleTypeSource;
    }

    public String getMultipleTypeSource() {
        return multipleTypeSource;
    }

    public Class getDestClass() {
        return destClass;
    }
}

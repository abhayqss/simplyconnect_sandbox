package com.scnsoft.eldermark.framework.connector4d.xml4Dconnector.template;

import com.scnsoft.eldermark.framework.connector4d.ColumnType4D;
import com.scnsoft.eldermark.framework.connector4d.xml4Dconnector.schema.ColumnMetadata;
import com.scnsoft.eldermark.framework.connector4d.xml4Dconnector.schema.CommunicationResponse;
import com.scnsoft.eldermark.framework.connector4d.xml4Dconnector.schema.Xml4DResultSetItem;
import sun.misc.BASE64Decoder;


import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by averazub on 5/24/2016.
 */
public class Xml4DResultSet implements ResultSet {

    // TODO use other Base64 implementation.
    // Read "Why Developers Should Not Write Programs That Call 'sun' Packages" for explanation http://www.oracle.com/technetwork/java/faq-sun-packages-142232.html
    private BASE64Decoder decoder;
    List<Xml4DResultSetItem> items;
    int iteratorCursor;
    Xml4DResultSetItem currentItem;
    List<ColumnMetadata> columnMetadataList;
    Map<String, Integer> columnNamesToIndices;

    private final String DATE_FORMAT_STR = "yyyy-MM-dd";
    private final String EMPTY_DATE = "0000-00-00";
    private final String DATETIME_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STR);
    private final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat(DATETIME_FORMAT_STR);


    public Xml4DResultSet() {
        items = new ArrayList<Xml4DResultSetItem>();
        iteratorCursor = -1;
        currentItem = null;
        decoder = new BASE64Decoder();
        columnMetadataList = new ArrayList<ColumnMetadata>();
        columnNamesToIndices = new HashMap<String, Integer>();
    }

    public static Xml4DResultSet readFromResponse(CommunicationResponse response) {
        Xml4DResultSet resultSet = new Xml4DResultSet();
        CommunicationResponse.Sqlrequest.Returnvarlist returnvarlist = response.getSqlrequest().getReturnvarlist();
        int size = returnvarlist.getArraysize()>0 ? returnvarlist.getArraysize() : 0;
        for (int i=0;i<size;i++) resultSet.items.add(new Xml4DResultSetItem());
        for (CommunicationResponse.Sqlrequest.Returnvarlist.Column column: returnvarlist.getColumn()) {
            String columnName = column.getName();
            Integer columnNumber = column.getNum();
            ColumnType4D type = ColumnType4D.getFromStringTypeSource(column.getType());
            resultSet.columnMetadataList.add(new ColumnMetadata(columnNumber, columnName, type, type.getDestClass() ));
            resultSet.columnNamesToIndices.put(columnName, columnNumber);
            String[] values = ((column.getValue()==null)||("".equals(column.getValue()))) ? new String[0] : column.getValue().split(",",-1);
            for (int j=0;j<values.length;j++) {
                if (resultSet.items.size()<=j) resultSet.items.add(new Xml4DResultSetItem());
                resultSet.items.get(j).pushValue(columnNumber, columnName, values[j]);
            }
        };
        return resultSet;
    }




    public boolean next() throws SQLException {
        iteratorCursor++;
        if (iteratorCursor >= items.size()) {
            return false;
        }
        currentItem = items.get(iteratorCursor);
        return true;
    }

    public String getString(int columnIndex) throws SQLException {
        String encoded = currentItem.getValue(columnIndex);
        if ((encoded==null) || ("".equals(encoded)) ) return encoded;
        try {
            return new String(decoder.decodeBuffer(encoded));
        } catch (IOException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        String encoded = currentItem.getValue(columnIndex);
        return (("1".equals(encoded))||("true".equalsIgnoreCase(encoded)));
    }

    public byte getByte(int columnIndex) throws SQLException {
        return (byte) getInt(columnIndex);
    }

    public short getShort(int columnIndex) throws SQLException {
        return (short) getInt(columnIndex);
    }

    public int getInt(int columnIndex) throws SQLException {
        String encoded = currentItem.getValue(columnIndex);
        return Integer.valueOf(encoded);
    }

    public long getLong(int columnIndex) throws SQLException {
        String encoded = currentItem.getValue(columnIndex);
        return Long.valueOf(encoded);
    }

    public float getFloat(int columnIndex) throws SQLException {
        String encoded = currentItem.getValue(columnIndex);
        return Float.valueOf(encoded);
    }

    public double getDouble(int columnIndex) throws SQLException {
        String encoded = currentItem.getValue(columnIndex);
        return Double.valueOf(encoded);
    }

    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        String encoded = currentItem.getValue(columnIndex);
        return encoded.getBytes();
    }

    public Date getDate(int columnIndex) throws SQLException {
        boolean isCoded = columnMetadataList.get(columnIndex-1).getColumnType4D().equals(ColumnType4D.STRING);
        String encoded = isCoded ? getString(columnIndex) : currentItem.getValue(columnIndex);
        if ((encoded==null)||("".equals(encoded))||(EMPTY_DATE.equals(encoded))) return null;
        encoded = encoded.trim();
        try {
            if (encoded.length()==DATE_FORMAT_STR.length()) {
                return new Date(DATE_FORMAT.parse(encoded).getTime());
            } else if (encoded.length()==DATETIME_FORMAT_STR.length()) {
                return new Date(DATETIME_FORMAT.parse(encoded).getTime());
            } else throw new ParseException("Cannot parse date: "+encoded,0);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Time getTime(int columnIndex) throws SQLException {
        long time = getInt(columnIndex)*1000l;
        return new Time(time);
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        int datetime = getInt(columnIndex);
        return new Timestamp(datetime);
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        String result = getString(columnIndex);
        if (result==null) return null;
        InputStream stream = new ByteArrayInputStream(result.getBytes(StandardCharsets.US_ASCII));
        return stream;
    }

    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        String result = getString(columnIndex);
        if (result==null) return null;
        InputStream stream = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_16));
        return stream;
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        String result = getString(columnIndex);
        if (result==null) return null;
        InputStream stream = new ByteArrayInputStream(result.getBytes());
        return stream;
    }

    public String getString(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getString(columnIndex);
    }

    public boolean getBoolean(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getBoolean(columnIndex);
    }

    public byte getByte(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getByte(columnIndex);
    }

    public short getShort(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getShort(columnIndex);
    }

    public int getInt(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getInt(columnIndex);
    }

    public long getLong(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getLong(columnIndex);
    }

    public float getFloat(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getFloat(columnIndex);
    }

    public double getDouble(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getDouble(columnIndex);
    }

    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getBigDecimal(columnIndex);
    }

    public byte[] getBytes(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getBytes(columnIndex);

    }

    public Date getDate(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getDate(columnIndex);
    }

    public Time getTime(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getTime(columnIndex);
    }

    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getTimestamp(columnIndex);
    }

    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getAsciiStream(columnIndex);
    }

    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getUnicodeStream(columnIndex);
    }

    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getBinaryStream(columnIndex);
    }


    public Object getObject(int columnIndex) throws SQLException {
        String valueStr = currentItem.getValue(columnIndex);
        if ((valueStr==null)||("".equals(valueStr))) return null;
        ColumnType4D type = columnMetadataList.get(columnIndex-1).getColumnType4D();
        switch (type) {
            case STRING:
                return getString(columnIndex);
            case BLOB:
            case PICTURE:
                return getBlob(columnIndex);
            case BOOLEAN:
                return getBoolean(columnIndex);
            case DATE:
                return getDate(columnIndex);
            case INT:
                return getInt(columnIndex);
            case LONG:
                return getLong(columnIndex);
            case REAL:
                return getDouble(columnIndex);
        }
        throw new SQLException("Type of column "+columnIndex+" not defined");
    }


    public int findColumn(String columnLabel) throws SQLException {
        return columnNamesToIndices.get(columnLabel);
    }

    public int getType() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Blob getBlob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Clob getClob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Array getArray(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        String valueStr = currentItem.getValue(columnIndex);
        if ((valueStr==null)||("".equals(valueStr))) return null;
        if (type.equals(Integer.class)) {
            return (T) Integer.valueOf(getInt(columnIndex));
        } else if (type.equals(Double.class)) {
            return (T) Double.valueOf(getDouble(columnIndex));
        } else if (type.equals(Long.class)) {
            return (T) Long.valueOf(getLong(columnIndex));
        } else if (type.equals(java.util.Date.class)) {
            return (T) new java.util.Date(getDate(columnIndex).getTime());
        } else if (type.equals(Date.class)) {
            return (T) getDate(columnIndex);
        } else if (type.equals(Boolean.class)) {
            return (T) Boolean.valueOf(getBoolean(columnIndex));
        } else if (type.equals(InputStream.class)) {
            return (T) getBinaryStream(columnIndex);
        } else if (type.equals(Byte.class)) {
            return (T) Byte.valueOf(getByte(columnIndex));
        } else if (type.equals(Byte[].class)) {
            byte[] bytes = getBytes(columnIndex);
            int arrlength = bytes.length;
            Byte[] outputArray = new Byte[arrlength];
            for(int i = 0; i < arrlength; ++i){
                outputArray[i] = bytes[i];
            }
            return (T)outputArray;
        } else if (type.equals(Reader.class)) {
            return (T) getCharacterStream(columnIndex);
        } else if (type.equals(Float.class)) {
            return (T) Float.valueOf(getFloat(columnIndex));
        } else if (type.equals(Short.class)) {
            return (T) Short.valueOf(getShort(columnIndex));
        } else if (type.equals(String.class)) {
            return (T) getString(columnIndex);
        } else if (type.equals(Time.class)) {
            return (T) getTime(columnIndex);
        } else if (type.equals(Timestamp.class)) {
            return (T) getTimestamp(columnIndex);
        } else if (type.equals(BigDecimal.class)) {
            return (T) getBigDecimal(columnIndex);
        }
        throw new SQLException("Unsupported type "+type.getName());
    }

    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getObject(columnIndex, type);
    }


    public Object getObject(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getObject(columnIndex);
    }


    public Reader getCharacterStream(int columnIndex) throws SQLException {
        String result = getString(columnIndex);
        if (result==null) return null;
        StringReader reader = new StringReader(result);
        return reader;
    }

    public Reader getCharacterStream(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getCharacterStream(columnIndex);
    }

    public int getRow() throws SQLException {
        return iteratorCursor;
    }



    public Blob getBlob(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getBlob(columnIndex);
    }

    public Clob getClob(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getClob(columnIndex);
    }

    public Array getArray(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getArray(columnIndex);
    }



    public String getNString(int columnIndex) throws SQLException {
        return getString(columnIndex);
    }

    public String getNString(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getNString(columnIndex);
    }

    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return getCharacterStream(columnIndex);
    }

    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getNCharacterStream(columnIndex);
    }




    ///---UNSUPPORTED


    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getConcurrency() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean rowUpdated() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean rowInserted() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean rowDeleted() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNull(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNull(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateString(String columnLabel, String x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getCursorName() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void insertRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void deleteRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void refreshRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void cancelRowUpdates() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void moveToInsertRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void moveToCurrentRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Statement getStatement() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        String encoded = currentItem.getValue(columnIndex);
        if ((encoded==null)||("".equals(encoded))) return null;
        return new BigDecimal(encoded);
    }

    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        int columnIndex = columnNamesToIndices.get(columnLabel);
        return getBigDecimal(columnIndex);
    }

    public boolean isBeforeFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isAfterLast() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isLast() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void beforeFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void afterLast() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean first() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean last() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean absolute(int row) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean relative(int rows) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean previous() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setFetchDirection(int direction) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getFetchDirection() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setFetchSize(int rows) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getFetchSize() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Ref getRef(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Ref getRef(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public URL getURL(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public URL getURL(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateRef(String columnLabel, Ref x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateClob(String columnLabel, Clob x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateArray(String columnLabel, Array x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public RowId getRowId(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public RowId getRowId(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getHoldability() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isClosed() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public NClob getNClob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public NClob getNClob(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void close() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean wasNull() throws SQLException {
        throw new UnsupportedOperationException();
    }

}

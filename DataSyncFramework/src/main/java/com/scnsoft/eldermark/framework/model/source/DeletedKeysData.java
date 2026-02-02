package com.scnsoft.eldermark.framework.model.source;

import java.util.Date;

public class DeletedKeysData {
    private long sequenceNum;
    private String uuid;
    private String tableName;
    private String keyName;
    private String keyValue;
    private Date dateTime;
    private Long recycleBinRecNum;

    public long getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(long sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Long getRecycleBinRecNum() {
        return recycleBinRecNum;
    }

    public void setRecycleBinRecNum(Long recycleBinRecNum) {
        this.recycleBinRecNum = recycleBinRecNum;
    }

    @Override
    public String toString() {
        return "DeletedKeysData{" +
                "sequenceNum=" + sequenceNum +
                ", uuid='" + uuid + '\'' +
                ", tableName='" + tableName + '\'' +
                ", keyName='" + keyName + '\'' +
                ", keyValue='" + keyValue + '\'' +
                ", dateTime=" + dateTime +
                ", recycleBinRecNum=" + recycleBinRecNum +
                '}';
    }
}

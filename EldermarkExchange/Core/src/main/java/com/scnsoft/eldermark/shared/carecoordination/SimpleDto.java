package com.scnsoft.eldermark.shared.carecoordination;

/**
 * Created by knetkachou on 3/24/2017.
 */
public class SimpleDto {
    Long id;
    String name;
    Long databaseId;

    public SimpleDto() {
    }

    public SimpleDto(Long id, String name, Long databaseId) {
        this.id = id;
        this.name = name;
        this.databaseId = databaseId;
    }


    public SimpleDto(Object id, Object name) {
        this.id = (Long)id;
        this.name = (String)name;
    }

    public SimpleDto(Object id, Object name, Object databaseId) {
        this.id = (Long)id;
        this.name = (String)name;
        this.databaseId = (Long)databaseId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Long databaseId) {
        this.databaseId = databaseId;
    }
}

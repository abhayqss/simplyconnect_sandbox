package com.scnsoft.eldermark.dto.events;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import org.springframework.data.domain.Sort;

public class EventOrNoteListItemDto {

    private Long id;
    private String clientName;
    private String typeName;
    private String typeTitle;
    private String entity;
    private String subTypeName;
    private String subTypeTitle;

    @DefaultSort(direction = Sort.Direction.DESC)
    private Long date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getSubTypeName() {
        return subTypeName;
    }

    public void setSubTypeName(String subTypeName) {
        this.subTypeName = subTypeName;
    }

    public String getSubTypeTitle() {
        return subTypeTitle;
    }

    public void setSubTypeTitle(String subTypeTitle) {
        this.subTypeTitle = subTypeTitle;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

}


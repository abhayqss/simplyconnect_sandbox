package com.scnsoft.eldermark.dto.notes;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledEntityDto;
import com.scnsoft.eldermark.entity.note.Note_;
import org.springframework.data.domain.Sort;

public class NoteDashboardListItemDto {
    private Long id;
    private String text;
    private NamedTitledEntityDto type;
    private NamedTitledEntityDto subType;
    @DefaultSort(direction = Sort.Direction.DESC)
    @EntitySort(Note_.LAST_MODIFIED_DATE)
    private Long date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public NamedTitledEntityDto getType() {
        return type;
    }

    public void setType(NamedTitledEntityDto type) {
        this.type = type;
    }

    public NamedTitledEntityDto getSubType() {
        return subType;
    }

    public void setSubType(NamedTitledEntityDto subType) {
        this.subType = subType;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}

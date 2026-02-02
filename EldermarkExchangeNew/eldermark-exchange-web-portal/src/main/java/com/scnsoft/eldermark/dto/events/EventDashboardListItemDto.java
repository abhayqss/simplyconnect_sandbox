package com.scnsoft.eldermark.dto.events;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledEntityDto;
import com.scnsoft.eldermark.entity.event.Event_;
import org.springframework.data.domain.Sort;

public class EventDashboardListItemDto {
    private Long id;
    private String type;
    @DefaultSort(direction = Sort.Direction.DESC)
    @EntitySort(Event_.EVENT_DATE_TIME)
    private Long date;
    private NamedTitledEntityDto group;
    private String author;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public NamedTitledEntityDto getGroup() {
        return group;
    }

    public void setGroup(NamedTitledEntityDto group) {
        this.group = group;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}

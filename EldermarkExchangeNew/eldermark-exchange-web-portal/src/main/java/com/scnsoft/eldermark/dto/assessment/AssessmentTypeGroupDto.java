package com.scnsoft.eldermark.dto.assessment;

import java.util.List;

public class AssessmentTypeGroupDto {
    private Long id;
    private String title;
    private List<AssessmentTypeDto> types;

    public AssessmentTypeGroupDto() {
    }

    public AssessmentTypeGroupDto(Long id, String title, List<AssessmentTypeDto> types) {
        this.id = id;
        this.title = title;
        this.types = types;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<AssessmentTypeDto> getTypes() {
        return types;
    }

    public void setTypes(List<AssessmentTypeDto> types) {
        this.types = types;
    }
}

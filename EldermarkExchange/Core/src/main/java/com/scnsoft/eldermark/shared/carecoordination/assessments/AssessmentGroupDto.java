package com.scnsoft.eldermark.shared.carecoordination.assessments;

import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;

import java.util.List;

public class AssessmentGroupDto {
    private Long id;

    private String name;

    private List<Pair<Long, String>> assessments;

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

    public List<Pair<Long, String>> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<Pair<Long, String>> assessments) {
        this.assessments = assessments;
    }
}

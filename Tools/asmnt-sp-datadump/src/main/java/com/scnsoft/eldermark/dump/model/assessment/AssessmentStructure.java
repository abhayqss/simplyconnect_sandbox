package com.scnsoft.eldermark.dump.model.assessment;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

public class AssessmentStructure {

    @JsonManagedReference("assessmentStructure")
    private List<AssessmentPage> pages;

    public List<AssessmentPage> getPages() {
        return pages;
    }

    public void setPages(List<AssessmentPage> pages) {
        this.pages = pages;
    }
}

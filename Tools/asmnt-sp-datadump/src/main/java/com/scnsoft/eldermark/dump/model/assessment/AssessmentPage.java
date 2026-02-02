package com.scnsoft.eldermark.dump.model.assessment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

public class AssessmentPage implements AssessmentElementsAware {

    private String name;

    @JsonManagedReference("parentPage")
    private List<AssessmentElement> elements;

    @JsonBackReference("assessmentStructure")
    private AssessmentStructure assessmentStructure;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AssessmentElement> getElements() {
        return elements;
    }

    public void setElements(List<AssessmentElement> elements) {
        this.elements = elements;
    }

    public AssessmentStructure getAssessmentStructure() {
        return assessmentStructure;
    }

    public void setAssessmentStructure(AssessmentStructure assessmentStructure) {
        this.assessmentStructure = assessmentStructure;
    }
}

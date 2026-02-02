package com.scnsoft.eldermark.dump.model.assessment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;


public class AssessmentElement implements AssessmentElementsAware {

    private String type;
    private String name;
    private String title;
    private Boolean isPriority;

    @JsonManagedReference("parentElement")
    private List<AssessmentElement> elements;

    @JsonBackReference("parentElement")
    private AssessmentElement parentElement;

    @JsonBackReference("parentPage")
    private AssessmentPage parentPage;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getIsPriority() {
        return isPriority;
    }

    public void setIsPriority(Boolean priority) {
        isPriority = priority;
    }

    public List<AssessmentElement> getElements() {
        return elements;
    }

    public void setElements(List<AssessmentElement> elements) {
        this.elements = elements;
    }

    public AssessmentElement getParentElement() {
        return parentElement;
    }

    public void setParentElement(AssessmentElement parentElement) {
        this.parentElement = parentElement;
    }

    public AssessmentPage getParentPage() {
        return parentPage;
    }

    public void setParentPage(AssessmentPage parentPage) {
        this.parentPage = parentPage;
    }
}

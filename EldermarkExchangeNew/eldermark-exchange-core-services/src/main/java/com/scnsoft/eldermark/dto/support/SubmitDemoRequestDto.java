package com.scnsoft.eldermark.dto.support;

import com.scnsoft.eldermark.entity.Employee;

public class SubmitDemoRequestDto {

    private Employee author;
    private String demoTitle;

    public SubmitDemoRequestDto(Employee author, String demoTitle) {
        this.author = author;
        this.demoTitle = demoTitle;
    }

    public Employee getAuthor() {
        return author;
    }

    public void setAuthor(Employee author) {
        this.author = author;
    }

    public String getDemoTitle() {
        return demoTitle;
    }

    public void setDemoTitle(String demoTitle) {
        this.demoTitle = demoTitle;
    }
}

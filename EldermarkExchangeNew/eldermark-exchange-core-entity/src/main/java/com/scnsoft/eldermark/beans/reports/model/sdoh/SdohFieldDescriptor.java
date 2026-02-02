package com.scnsoft.eldermark.beans.reports.model.sdoh;

public class SdohFieldDescriptor {

    private boolean required;
    private Integer length;

    public SdohFieldDescriptor(boolean required) {
        this.required = required;
    }

    public SdohFieldDescriptor(boolean required, Integer length) {
        this.required = required;
        this.length = length;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }
}

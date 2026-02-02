package com.scnsoft.eldermark.dto.singature;

import java.util.List;

public class DocumentSignatureTemplateFieldProperties {
    private String label;
    private List<String> values;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}

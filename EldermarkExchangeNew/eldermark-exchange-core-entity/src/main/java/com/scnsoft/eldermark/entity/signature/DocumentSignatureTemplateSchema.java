package com.scnsoft.eldermark.entity.signature;

import java.util.Map;

public class DocumentSignatureTemplateSchema {

    private String formSchema;
    private String uiFormSchema;
    private Map<String, Object> defaultValues;

    public String getFormSchema() {
        return formSchema;
    }

    public void setFormSchema(String formSchema) {
        this.formSchema = formSchema;
    }

    public String getUiFormSchema() {
        return uiFormSchema;
    }

    public void setUiFormSchema(String uiFormSchema) {
        this.uiFormSchema = uiFormSchema;
    }

    public Map<String, Object> getDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(Map<String, Object> defaultValues) {
        this.defaultValues = defaultValues;
    }
}

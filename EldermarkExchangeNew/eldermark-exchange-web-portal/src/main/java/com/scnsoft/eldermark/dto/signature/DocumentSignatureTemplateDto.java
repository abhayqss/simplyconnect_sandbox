package com.scnsoft.eldermark.dto.signature;

import java.util.Map;

public class DocumentSignatureTemplateDto extends BaseDocumentSignatureTemplateDto {

    private Long id;
    private String name;
    private String schema;
    private String uiSchema;
    private Map<String, Object> defaults;
    private Boolean hasSignatureAreas;

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

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getUiSchema() {
        return uiSchema;
    }

    public void setUiSchema(String uiSchema) {
        this.uiSchema = uiSchema;
    }

    public Map<String, Object> getDefaults() {
        return defaults;
    }

    public void setDefaults(Map<String, Object> defaults) {
        this.defaults = defaults;
    }

    public Boolean getHasSignatureAreas() {
        return hasSignatureAreas;
    }

    public void setHasSignatureAreas(Boolean hasSignatureAreas) {
        this.hasSignatureAreas = hasSignatureAreas;
    }
}

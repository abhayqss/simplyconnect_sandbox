package com.scnsoft.eldermark.entity.signature;

import com.scnsoft.eldermark.beans.projection.IdAware;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "DocumentSignatureTemplateToolboxRequesterFieldType")
public class DocumentSignatureTemplateToolboxRequesterFieldType implements Serializable, IdAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false)
    private ToolboxRequesterFieldTypeCode code;

    @Enumerated(EnumType.STRING)
    @Column(name = "sc_field_type", nullable = false)
    private ScSourceTemplateFieldType scFieldType;

    @Column(name = "json_schema", nullable = false)
    private String jsonSchema;

    @Column(name = "json_ui_schema", nullable = false)
    private String jsonUiSchema;

    @Override
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

    public ToolboxRequesterFieldTypeCode getCode() {
        return code;
    }

    public void setCode(ToolboxRequesterFieldTypeCode code) {
        this.code = code;
    }

    public ScSourceTemplateFieldType getScFieldType() {
        return scFieldType;
    }

    public void setScFieldType(ScSourceTemplateFieldType scFieldType) {
        this.scFieldType = scFieldType;
    }

    public String getJsonSchema() {
        return jsonSchema;
    }

    public void setJsonSchema(String jsonSchemaTemplate) {
        this.jsonSchema = jsonSchemaTemplate;
    }

    public String getJsonUiSchema() {
        return jsonUiSchema;
    }

    public void setJsonUiSchema(String jsonUiSchemaTemplate) {
        this.jsonUiSchema = jsonUiSchemaTemplate;
    }
}

package com.scnsoft.eldermark.entity.signature;

import com.scnsoft.eldermark.beans.projection.IdAware;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DocumentSignatureTemplateAutoFillFieldType")
public class DocumentSignatureTemplateAutoFillFieldType implements Serializable, IdAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false)
    private TemplateFieldDefaultValueType code;

    @Enumerated(EnumType.STRING)
    @Column(name = "sc_field_type", nullable = false)
    private ScSourceTemplateFieldType scFieldType;

    @Column(name = "json_schema")
    private String jsonSchema;

    @Column(name = "json_ui_schema")
    private String jsonUiSchema;

    @Column(name = "position", nullable = false)
    private Long position;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public TemplateFieldDefaultValueType getCode() {
        return code;
    }

    public void setCode(final TemplateFieldDefaultValueType code) {
        this.code = code;
    }

    public ScSourceTemplateFieldType getScFieldType() {
        return scFieldType;
    }

    public void setScFieldType(final ScSourceTemplateFieldType type) {
        this.scFieldType = type;
    }

    public String getJsonSchema() {
        return jsonSchema;
    }

    public void setJsonSchema(String jsonSchema) {
        this.jsonSchema = jsonSchema;
    }

    public String getJsonUiSchema() {
        return jsonUiSchema;
    }

    public void setJsonUiSchema(String jsonUiSchema) {
        this.jsonUiSchema = jsonUiSchema;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long order) {
        this.position = order;
    }
}

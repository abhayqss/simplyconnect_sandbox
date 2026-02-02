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
@Table(name = "DocumentSignatureTemplateToolboxSignerFieldType")
public class DocumentSignatureTemplateToolboxSignerFieldType implements Serializable, IdAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false)
    private TemplateFieldPdcFlowType code;

    @Column(name = "width")
    private Short width;

    @Column(name = "height")
    private Short height;

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

    public TemplateFieldPdcFlowType getCode() {
        return code;
    }

    public void setCode(final TemplateFieldPdcFlowType code) {
        this.code = code;
    }

    public Short getWidth() {
        return width;
    }

    public void setWidth(final Short width) {
        this.width = width;
    }

    public Short getHeight() {
        return height;
    }

    public void setHeight(final Short height) {
        this.height = height;
    }
}

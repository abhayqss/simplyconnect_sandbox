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
@Table(name = "DocumentSignatureTemplateOrganizationAutoFillFieldType")
public class DocumentSignatureTemplateOrganizationAutoFillFieldType implements Serializable, IdAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false)
    private ScSourceTemplateFieldType code;

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

    public ScSourceTemplateFieldType getCode() {
        return code;
    }

    public void setCode(final ScSourceTemplateFieldType code) {
        this.code = code;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long order) {
        this.position = order;
    }
}

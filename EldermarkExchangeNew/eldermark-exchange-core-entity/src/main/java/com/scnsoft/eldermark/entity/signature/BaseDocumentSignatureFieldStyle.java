package com.scnsoft.eldermark.entity.signature;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public class BaseDocumentSignatureFieldStyle implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TemplateFieldStyleType type;

    @Column(name = "value")
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public TemplateFieldStyleType getType() {
        return type;
    }

    public void setType(final TemplateFieldStyleType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}

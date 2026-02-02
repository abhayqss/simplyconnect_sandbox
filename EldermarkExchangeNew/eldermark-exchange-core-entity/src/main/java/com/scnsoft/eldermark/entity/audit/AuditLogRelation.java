package com.scnsoft.eldermark.entity.audit;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AuditLogRelation<T extends Object & Serializable> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public abstract List<T> getRelatedIds();
    public abstract List<String> getAdditionalFields();
    public abstract AuditLogType getConverterType();
}
package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "AnyCcdCode")
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class AnyCcdCode implements Serializable, ConceptDescriptor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public abstract String getCode();

    public abstract void setCode(String code);

    public abstract String getCodeSystem();

    public abstract void setCodeSystem(String codeSystem);

    public abstract String getDisplayName();

    public abstract void setDisplayName(String displayName);

    public abstract String getCodeSystemName();

    public abstract void setCodeSystemName(String codeSystemName);

}

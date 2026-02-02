package com.scnsoft.eldermark.entity;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

@Entity
/*
@Cacheable
@org.hibernate.annotations.Cache(usage= CacheConcurrencyStrategy.READ_WRITE, region="setuptables")
*/
public class Role implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="name", nullable = false, unique = true)
    private RoleCode code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleCode getCode() {
        return code;
    }

    public void setCode(RoleCode code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        return code == role.code;

    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }
}

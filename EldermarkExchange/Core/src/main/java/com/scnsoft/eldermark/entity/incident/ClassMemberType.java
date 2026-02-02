package com.scnsoft.eldermark.entity.incident;

import javax.persistence.*;

import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "ClassMemberType")
@Immutable
public class ClassMemberType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

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
}

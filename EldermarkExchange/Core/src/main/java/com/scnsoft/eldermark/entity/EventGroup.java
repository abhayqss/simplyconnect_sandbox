package com.scnsoft.eldermark.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Immutable
@Entity
public class EventGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "is_service", nullable = false)
    private Boolean service;

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

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getService() {
        return service;
    }

    public void setService(Boolean service) {
        this.service = service;
    }
}

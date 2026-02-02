package com.scnsoft.eldermark.dump.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Immutable
@Entity
@Table(name = "EventType")
public class EventType implements Serializable {

    private static final long serialVersionUID = 1762706627600836755L;

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", length = 50, nullable = false)
    private EventTypeEnum code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventTypeEnum getCode() {
        return code;
    }

    public void setCode(EventTypeEnum code) {
        this.code = code;
    }
}

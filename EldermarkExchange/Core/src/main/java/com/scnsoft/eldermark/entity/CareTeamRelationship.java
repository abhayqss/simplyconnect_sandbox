package com.scnsoft.eldermark.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author phomal
 * Created on 5/3/2017
 */
@Entity
@Table(name = "CareTeamRelationship")
public class CareTeamRelationship implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false)
    private Relationship code;

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

    public Relationship getCode() {
        return code;
    }

    public void setCode(Relationship code) {
        this.code = code;
    }

    /**
     * Member role. MEDICAL_STAFF = Doctor, FRIEND_FAMILY = Relative = Friend or Family Member
     */
    public enum Relationship {
        FRIEND_FAMILY("FRIEND_FAMILY"),
        MEDICAL_STAFF("MEDICAL_STAFF");

        private final String value;

        Relationship(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return value;
        }

        @JsonCreator
        public static Relationship fromValue(String text) {
            for (Relationship b : Relationship.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }
}

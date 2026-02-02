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
@Table(name = "CareTeamRelation")
public class CareTeamRelation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name="code", nullable = false)
    private Relation code;

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

    public Relation getCode() {
        return code;
    }

    public void setCode(Relation code) {
        this.code = code;
    }

    /**
     * This attribute indicates the nature of the relationship between a patient and a CTM
     */
    public enum Relation {
      FAMILY("Family"),
      FRIEND("Friend"),
      GUARDIAN("Guardian"),
      PARTNER("Partner"),
      WORK("Work"),
      PARENT("Parent");

      private final String value;

      Relation(String value) {
        this.value = value;
      }

      @Override
      @JsonValue
      public String toString() {
        return value;
      }

      @JsonCreator
      public static Relation fromValue(String text) {
        for (Relation b : Relation.values()) {
          if (String.valueOf(b.value).equals(text)) {
            return b;
          }
        }
        return null;
      }
    }
}

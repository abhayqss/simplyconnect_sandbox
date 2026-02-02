package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "AnyCcdCode")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public abstract class AnyCcdCode implements Serializable, ConceptDescriptor {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
}

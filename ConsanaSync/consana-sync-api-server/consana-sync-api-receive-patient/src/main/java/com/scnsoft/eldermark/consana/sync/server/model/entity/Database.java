package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Table(name = "SourceDatabase")
@Data
@NoArgsConstructor
public class Database {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    private Long id;

    @Column(name = "consana_xowning_id")
    private String consanaXOwningId;

    @Column(name = "alternative_id", length = 255)
    private String alternativeId;

}

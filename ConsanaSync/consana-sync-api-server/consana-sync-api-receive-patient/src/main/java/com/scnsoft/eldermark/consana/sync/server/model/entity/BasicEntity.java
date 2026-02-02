package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * This abstract class is the base class for all Entities in Eldermark Exchange Web application.
 */
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class BasicEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "database_id", nullable = false, insertable = true, updatable = true)
    private Database database;

//    @Column(name = "database_id", nullable = false, insertable = false, updatable = false)
//    private long databaseId;

    public BasicEntity(Long id) {
        this.id = id;
    }

}

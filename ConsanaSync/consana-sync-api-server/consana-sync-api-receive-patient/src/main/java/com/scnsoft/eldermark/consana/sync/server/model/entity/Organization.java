package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "database_id", nullable = false)
    private Database database;

    @Column(name = "consana_org_id")
    private String consanaOrgId;

    @Column(name = "is_consana_enabled", nullable = false)
    private Boolean isConsanaIntegrationEnabled;

    @Column(name = "is_consana_initial_sync")
    private Boolean isConsanaInitialSync;
}

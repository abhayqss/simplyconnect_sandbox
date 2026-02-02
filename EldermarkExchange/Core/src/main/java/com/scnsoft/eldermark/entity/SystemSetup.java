package com.scnsoft.eldermark.entity;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
/*
@Cacheable
@org.hibernate.annotations.Cache(usage= CacheConcurrencyStrategy.READ_WRITE, region="database")
*/
@Table(name = "SystemSetup")
public class SystemSetup {
    @Id
    @Column(name = "database_id")
    private Long databaseId;

    @OneToOne
    @JoinColumn(name="database_id")
    public Database database;

    @Column(name = "login_company_id", length = 10, nullable = false)
    private String loginCompanyId;

    public String getLoginCompanyId() {
        return loginCompanyId;
    }

    public void setLoginCompanyId(String loginCompanyId) {
        this.loginCompanyId = loginCompanyId;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public Long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Long databaseId) {
        this.databaseId = databaseId;
    }
}

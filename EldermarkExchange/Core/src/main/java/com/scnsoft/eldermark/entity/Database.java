package com.scnsoft.eldermark.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

@Entity
/*
@Cacheable
@org.hibernate.annotations.Cache(usage= CacheConcurrencyStrategy.READ_WRITE, region="database")
*/
@Table(name = "SourceDatabase")
@NamedQueries({
        @NamedQuery(name = "db.getPrimaryBriefList", query = "Select distinct db.id, db.name from Database db, AffiliatedOrganizations ao " +
                "where db.id = ao.primaryDatabaseId and ao.affiliatedDatabaseId in :databaseIds"),
        @NamedQuery(name = "db.getAffiliatedBriefList", query = "Select distinct db.id, db.name from Database db, AffiliatedOrganizations ao " +
                "where db.id = ao.affiliatedDatabaseId and ao.primaryDatabaseId in :databaseIds"),
        @NamedQuery(name = "db.getPrimaryCount", query = "select count(distinct db.id) from Database db, AffiliatedOrganizations ao " +
                "where db.id = ao.primaryDatabaseId and ao.affiliatedDatabaseId = :employeeDbIds"),
        @NamedQuery(name = "db.checkDatabaseAccess", query = "select count (id) from AffiliatedOrganizations ao " +
                "where ao.affiliatedDatabaseId in :employeeDbIds and ao.primaryDatabaseId = :databaseId")
})
public class Database {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 255, nullable = false, unique = true)
    private String name;

    /**
     *  An alternative id that should be used to refer to this entity ONLY by entities that cannot be restored from
     *  the original database (resident's documents). If resident's documents referred to this entity by its primary id,
     *  reimporting data would break those references, since the id would be changed after reimporting.
     *
     *  DO NOT refer to this entity by the alternative id until you know what you're doing.
     *  In usual cases instead refer to it by its id (primary key).
     */
    @Column(name = "alternative_id", length = 255)
    private String alternativeId;

    @Column(name = "url", length = 255, unique = true)
    private String url;

    @Column(name = "is_service")
    private Boolean isService;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "direct_config_id", referencedColumnName = "id", updatable=true)
    private DirectConfiguration directConfig;

    @OneToOne(mappedBy="database", cascade = CascadeType.ALL, fetch=FetchType.LAZY, optional=true)
    public SystemSetup systemSetup;

    @Column(name = "is_eldermark")
    private boolean isEldermark;

    @Column(name = "main_logo_path")
    private String mainLogoPath;

    @Column(name = "additional_logo_path")
    private String additionalLogoPath;

    @Column(name = "external_logo_id")
    private String externalLogoId;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_and_contacts_id", referencedColumnName = "id", updatable=true)
    private SourceDatabaseAddressAndContacts addressAndContacts;

    @Column(name = "oid")
    private String oid;

    @Column(name = "last_modified")
    private Date lastModified;

    @Column(name = "created_automatically")
    private Boolean createdAutomatically;

    @Column(name = "copy_event_notifications_for_patients")
    private Boolean copyEventNotificationsForPatients;
/*
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "id", referencedColumnName = "database_id")
    private DatabaseOrgCountEntity databaseOrgCountEntity;*/

    @Column(name = "remote_host", length = 100)
    private String remoteHost;

    @Column(name = "remote_port")
    private Integer remotePort;

    @Column(name = "remote_username", length = 100)
    private String remoteUsername;

    @Column(name = "remote_password", length = 200)
    private String remotePassword;

    @Column(name = "remote_use_ssl")
    private Boolean remoteUseSsl;

    @Column(name = "max_days_to_process_appointment")
    private Integer maxDaysToProcessAppointment;

    @ElementCollection
    @CollectionTable(name = "ReportingGoverningEmail",
            joinColumns = @JoinColumn(name = "database_id"))
    @Column(name = "email")
    private List<String> reportingGoverningEmails;
    
    @Column(name = "is_initial_sync")
    private Boolean isInitialSync;

    @Column(name = "koble_integration")
    private Boolean isKobleIntegrationEnabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlternativeId() {
        return alternativeId;
    }

    public void setAlternativeId(String alternativeId) {
        this.alternativeId = alternativeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getService() {
        return isService;
    }

    public void setService(Boolean service) {
        isService = service;
    }

    public DirectConfiguration getDirectConfig() {
        return directConfig;
    }

    public void setDirectConfig(DirectConfiguration directConfig) {
        this.directConfig = directConfig;
    }

    public boolean isEldermark() {
        return isEldermark;
    }

    public void setEldermark(boolean eldermark) {
        isEldermark = eldermark;
    }

    public SystemSetup getSystemSetup() {
        return systemSetup;
    }

    public void setSystemSetup(SystemSetup systemSetup) {
        this.systemSetup = systemSetup;
    }

    public String getMainLogoPath() {
        return mainLogoPath;
    }

    public void setMainLogoPath(String mainLogoPath) {
        this.mainLogoPath = mainLogoPath;
    }

    public String getAdditionalLogoPath() {
        return additionalLogoPath;
    }

    public void setAdditionalLogoPath(String additionalLogoPath) {
        this.additionalLogoPath = additionalLogoPath;
    }

    public String getExternalLogoId() {
        return externalLogoId;
    }

    public void setExternalLogoId(String externalLogoId) {
        this.externalLogoId = externalLogoId;
    }

    public Boolean getIsService() {
        return isService;
    }

    public void setIsService(Boolean isService) {
        this.isService = isService;
    }

    public void setIsEldermark(boolean isEldermark) {
        this.isEldermark = isEldermark;
    }

    public SourceDatabaseAddressAndContacts getAddressAndContacts() {
        return addressAndContacts;
    }

    public void setAddressAndContacts(SourceDatabaseAddressAndContacts addressAndContacts) {
        this.addressAndContacts = addressAndContacts;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Boolean getCreatedAutomatically() {
        return createdAutomatically;
    }

    public void setCreatedAutomatically(Boolean createdAutomatically) {
        this.createdAutomatically = createdAutomatically;
    }
/*
    public DatabaseOrgCountEntity getDatabaseOrgCountEntity() {
        return databaseOrgCountEntity;
    }

    public void setDatabaseOrgCountEntity(DatabaseOrgCountEntity databaseOrgCountEntity) {
        this.databaseOrgCountEntity = databaseOrgCountEntity;
    }*/

    public Boolean getCopyEventNotificationsForPatients() {
        return copyEventNotificationsForPatients;
    }

    public void setCopyEventNotificationsForPatients(Boolean copyEventNotificationsForPatients) {
        this.copyEventNotificationsForPatients = copyEventNotificationsForPatients;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public Integer getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(Integer remotePort) {
        this.remotePort = remotePort;
    }

    public String getRemoteUsername() {
        return remoteUsername;
    }

    public void setRemoteUsername(String remoteUsername) {
        this.remoteUsername = remoteUsername;
    }

    public String getRemotePassword() {
        return remotePassword;
    }

    public void setRemotePassword(String remotePassword) {
        this.remotePassword = remotePassword;
    }

    public Boolean getRemoteUseSsl() {
        return remoteUseSsl;
    }

    public void setRemoteUseSsl(Boolean remoteUseSsl) {
        this.remoteUseSsl = remoteUseSsl;
    }

    public Integer getMaxDaysToProcessAppointment() {
        return maxDaysToProcessAppointment;
    }

    public void setMaxDaysToProcessAppointment(Integer maxDaysToProcessAppointment) {
        this.maxDaysToProcessAppointment = maxDaysToProcessAppointment;
    }

    public List<String> getReportingGoverningEmails() {
        return reportingGoverningEmails;
    }

    public void setReportingGoverningEmails(List<String> reportingGoverningEmails) {
        this.reportingGoverningEmails = reportingGoverningEmails;
    }
    
    public Boolean getKobleIntegrationEnabled() {
        return isKobleIntegrationEnabled;
    }

    public void setKobleIntegrationEnabled(Boolean kobleIntegrationEnabled) {
        isKobleIntegrationEnabled = kobleIntegrationEnabled;
    }

    public Boolean getIsInitialSync() {
		return isInitialSync;
	}

	public void setIsInitialSync(Boolean isInitialSync) {
		this.isInitialSync = isInitialSync;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Database database = (Database) o;

        return new EqualsBuilder()
                .append(getId(), database.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .toHashCode();
    }
}

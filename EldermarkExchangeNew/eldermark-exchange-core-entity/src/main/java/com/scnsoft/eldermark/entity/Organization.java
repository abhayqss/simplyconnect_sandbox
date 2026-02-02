package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.beans.projection.IdNameAware;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
/*
@Cacheable
@org.hibernate.annotations.Cache(usage= CacheConcurrencyStrategy.READ_WRITE, region="database")
*/
@Table(name = "SourceDatabase")

public class Organization implements IdNameAware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 256, nullable = false, unique = true)
    private String name;

    /**
     * An alternative id that should be used to refer to this entity ONLY by entities that cannot be restored from
     * the original database (resident's documents). If resident's documents referred to this entity by its primary id,
     * reimporting data would break those references, since the id would be changed after reimporting.
     * <p>
     * DO NOT refer to this entity by the alternative id until you know what you're doing.
     * In usual cases instead refer to it by its id (primary key).
     */

    @Column(name = "alternative_id", length = 255)
    private String alternativeId;

    @Column(name = "url", length = 255, unique = true)
    private String url;

    @Column(name = "is_service")
    private Boolean isService;

    @OneToOne(optional = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "direct_config_id", referencedColumnName = "id", updatable = true)
    private DirectConfiguration directConfig;

    @OneToOne(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
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
    @JoinColumn(name = "address_and_contacts_id", referencedColumnName = "id", columnDefinition = "int")
    private SourceDatabaseAddressAndContacts addressAndContacts;

    @Column(name = "oid")
    private String oid;

    @Column(name = "last_modified")
    private Instant lastModified;

    @Column(name = "created_automatically")
    private Boolean createdAutomatically;

    @Column(name = "copy_event_notifications_for_patients")
    private Boolean copyEventNotificationsForPatients;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "database_id")
    private DatabaseOrgCountEntity databaseOrgCountEntity;

    @Column(name = "max_days_to_process_appointment")
    private Integer maxDaysToProcessAppointment;

    @Column(name = "koble_integration")
    private Boolean isKobleIntegrationEnabled;

    @Column(name = "receive_non_network_referrals")
    private boolean receiveNonNetworkReferrals;

    @Column(name = "labs_enabled")
    private boolean labsEnabled;

    @Column(name = "is_labs_research_testing")
    private boolean isLabsResearchTesting;

    @Column(name = "sdoh_reports_enabled")
    private boolean sdohReportsEnabled;

    @Column(name = "sdoh_submitter_name")
    private String sdohSubmitterName;

    @Column(name = "sdoh_zoneId")
    private String sdohZoneId;

    @Column(name = "sdoh_source_system")
    private String sdohSourceSystem;

    @Column(name = "is_chat_enabled", nullable = false) //use default DB value during create
    private boolean isChatEnabled;

    @Column(name = "is_video_enabled", nullable = false) //use default DB value during create
    private boolean isVideoEnabled;

    @Column(name = "is_signature_enabled", nullable = false) //use default DB value during create
    private boolean isSignatureEnabled;

    @ElementCollection
    @CollectionTable(name = "ReportingGoverningEmail",
            joinColumns = @JoinColumn(name = "database_id"))
    @Column(name = "email")
    private List<String> reportingGoverningEmails;

    @Column(name = "consana_xowning_id")
    private String consanaXOwningId;

    @Column(name = "is_experience_center_enabled")
    private Boolean isPaperlessHealthcareEnabled;

    @Column(name = "is_appointments_enabled", nullable = false)
    private Boolean isAppointmentsEnabled;

    @Column(name = "exclude_from_record_search", nullable = false)
    private boolean excludeFromRecordSearch;

    @Column(name = "are_release_notes_enabled")
    private Boolean areReleaseNotesEnabled;

    @Column(name = "pcc_org_uuid")
    private String pccOrgUuid;

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

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public Boolean getCreatedAutomatically() {
        return createdAutomatically;
    }

    public void setCreatedAutomatically(Boolean createdAutomatically) {
        this.createdAutomatically = createdAutomatically;
    }

    public DatabaseOrgCountEntity getDatabaseOrgCountEntity() {
        return databaseOrgCountEntity;
    }

    public void setDatabaseOrgCountEntity(DatabaseOrgCountEntity databaseOrgCountEntity) {
        this.databaseOrgCountEntity = databaseOrgCountEntity;
    }

    public Boolean getCopyEventNotificationsForPatients() {
        return copyEventNotificationsForPatients;
    }

    public void setCopyEventNotificationsForPatients(Boolean copyEventNotificationsForPatients) {
        this.copyEventNotificationsForPatients = copyEventNotificationsForPatients;
    }

    public Integer getMaxDaysToProcessAppointment() {
        return maxDaysToProcessAppointment;
    }

    public void setMaxDaysToProcessAppointment(Integer maxDaysToProcessAppointment) {
        this.maxDaysToProcessAppointment = maxDaysToProcessAppointment;
    }

    public boolean isReceiveNonNetworkReferrals() {
        return receiveNonNetworkReferrals;
    }

    public void setReceiveNonNetworkReferrals(boolean receiveNonNetworkReferrals) {
        this.receiveNonNetworkReferrals = receiveNonNetworkReferrals;
    }

    public Boolean getKobleIntegrationEnabled() {
        return isKobleIntegrationEnabled;
    }

    public void setKobleIntegrationEnabled(Boolean kobleIntegrationEnabled) {
        isKobleIntegrationEnabled = kobleIntegrationEnabled;
    }

    public boolean getLabsEnabled() {
        return labsEnabled;
    }

    public void setLabsEnabled(boolean labsEnabled) {
        this.labsEnabled = labsEnabled;
    }

    public boolean isLabsResearchTesting() {
        return isLabsResearchTesting;
    }

    public void setLabsResearchTesting(boolean labsResearchTesting) {
        isLabsResearchTesting = labsResearchTesting;
    }

    public boolean isSdohReportsEnabled() {
        return sdohReportsEnabled;
    }

    public void setSdohReportsEnabled(boolean sdohReportsEnabled) {
        this.sdohReportsEnabled = sdohReportsEnabled;
    }

    public String getSdohSubmitterName() {
        return sdohSubmitterName;
    }

    public void setSdohSubmitterName(String sdohSubmitterName) {
        this.sdohSubmitterName = sdohSubmitterName;
    }

    public String getSdohZoneId() {
        return sdohZoneId;
    }

    public void setSdohZoneId(String sdohZoneId) {
        this.sdohZoneId = sdohZoneId;
    }

    public String getSdohSourceSystem() {
        return sdohSourceSystem;
    }

    public void setSdohSourceSystem(String sdohSourceSystem) {
        this.sdohSourceSystem = sdohSourceSystem;
    }

    public boolean isChatEnabled() {
        return isChatEnabled;
    }

    public void setChatEnabled(boolean chatEnabled) {
        isChatEnabled = chatEnabled;
    }

    public boolean isVideoEnabled() {
        return isVideoEnabled;
    }

    public void setVideoEnabled(boolean videoEnabled) {
        isVideoEnabled = videoEnabled;
    }

    public boolean isSignatureEnabled() {
        return isSignatureEnabled;
    }

    public void setSignatureEnabled(boolean signatureEnabled) {
        isSignatureEnabled = signatureEnabled;
    }

    public Boolean getIsAppointmentsEnabled() {
        return isAppointmentsEnabled;
    }

    public void setIsAppointmentsEnabled(Boolean appointmentsEnabled) {
        isAppointmentsEnabled = appointmentsEnabled;
    }

    public List<String> getReportingGoverningEmails() {
        return reportingGoverningEmails;
    }

    public void setReportingGoverningEmails(List<String> reportingGoverningEmails) {
        this.reportingGoverningEmails = reportingGoverningEmails;
    }

    public String getConsanaXOwningId() {
        return consanaXOwningId;
    }

    public void setConsanaXOwningId(String consanaXOwningId) {
        this.consanaXOwningId = consanaXOwningId;
    }

    public Boolean getIsPaperlessHealthcareEnabled() {
        return isPaperlessHealthcareEnabled;
    }

    public void setIsPaperlessHealthcareEnabled(Boolean paperlessHealthcareEnabled) {
        isPaperlessHealthcareEnabled = paperlessHealthcareEnabled;
    }

    public boolean getExcludeFromRecordSearch() {
        return excludeFromRecordSearch;
    }

    public void setExcludeFromRecordSearch(boolean excludeFromRecordSearch) {
        this.excludeFromRecordSearch = excludeFromRecordSearch;
    }

    public Boolean getAreReleaseNotesEnabled() {
        return areReleaseNotesEnabled;
    }

    public void setAreReleaseNotesEnabled(Boolean areReleaseNotesEnabled) {
        this.areReleaseNotesEnabled = areReleaseNotesEnabled;
    }

    public String getPccOrgUuid() {
        return pccOrgUuid;
    }

    public void setPccOrgUuid(String pccOrgUuid) {
        this.pccOrgUuid = pccOrgUuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Organization database = (Organization) o;

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

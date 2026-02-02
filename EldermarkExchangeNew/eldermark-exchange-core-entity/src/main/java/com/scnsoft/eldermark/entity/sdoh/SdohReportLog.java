package com.scnsoft.eldermark.entity.sdoh;

import com.scnsoft.eldermark.entity.Organization;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "SdohReportLog")
public class SdohReportLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "database_id")
    private Organization organization;

    @Column(name = "database_id", insertable = false, updatable = false)
    private Long organizationId;

    @Column(name = "period_start")
    private Instant periodStart;

    @Column(name = "period_end")
    private Instant periodEnd;

    @Column(name = "sent_to_uhc_datetime")
    private Instant sentToUhcDatetime;

    @Column(name = "last_zip_download_at")
    private Instant lastZipDownloadAt;

    @Column(name = "last_zip_download_submitter_name")
    private String lastZipDownloadSubmitterName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Instant getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(Instant periodStart) {
        this.periodStart = periodStart;
    }

    public Instant getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Instant periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Instant getSentToUhcDatetime() {
        return sentToUhcDatetime;
    }

    public void setSentToUhcDatetime(Instant sentToUhcDatetime) {
        this.sentToUhcDatetime = sentToUhcDatetime;
    }

    public Instant getLastZipDownloadAt() {
        return lastZipDownloadAt;
    }

    public void setLastZipDownloadAt(Instant lastZipDownloadAt) {
        this.lastZipDownloadAt = lastZipDownloadAt;
    }

    public String getLastZipDownloadSubmitterName() {
        return lastZipDownloadSubmitterName;
    }

    public void setLastZipDownloadSubmitterName(String lastZipDownloadSubmitterName) {
        this.lastZipDownloadSubmitterName = lastZipDownloadSubmitterName;
    }
}

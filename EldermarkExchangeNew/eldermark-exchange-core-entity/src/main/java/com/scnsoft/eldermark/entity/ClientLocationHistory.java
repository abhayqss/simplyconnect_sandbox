package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "ResidentLocationHistory")
public class ClientLocationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "resident_id", nullable = false)
    private Long clientId;

    @ManyToOne
    @JoinColumn(name = "resident_id", nullable = false, updatable = false, insertable = false)
    private Client client;

    @Column(name = "record_datetime", nullable = false)
    private Instant recordDatetime;

    @Column(name = "seen_datetime", nullable = false)
    private Instant seenDatetime;

    @Column(name = "reported_by", nullable = false)
    private Long reportedById;

    @ManyToOne
    @JoinColumn(name = "reported_by", nullable = false, updatable = false, insertable = false)
    private Employee reportedBy;

    @Column(name = "longitude", columnDefinition = "decimal(10,4)")
    private BigDecimal longitude;

    @Column(name = "latitude", columnDefinition = "decimal(10,4)")
    private BigDecimal latitude;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Instant getRecordDatetime() {
        return recordDatetime;
    }

    public void setRecordDatetime(Instant recordDatetime) {
        this.recordDatetime = recordDatetime;
    }

    public Instant getSeenDatetime() {
        return seenDatetime;
    }

    public void setSeenDatetime(Instant seenDatetime) {
        this.seenDatetime = seenDatetime;
    }

    public Long getReportedById() {
        return reportedById;
    }

    public void setReportedById(Long reportedById) {
        this.reportedById = reportedById;
    }

    public Employee getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(Employee reportedBy) {
        this.reportedBy = reportedBy;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
}

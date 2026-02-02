package com.scnsoft.eldermark.entity.referral;

import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ReferralRequestNotification")
public class ReferralRequestNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "created_datetime", nullable = false)
    private Instant createdDatetime;

    @ManyToOne
    @JoinColumn(name = "referral_request_id")
    private ReferralRequest referralRequest;

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    @Column(name = "sent_datetime")
    private Instant sentDatetime;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 50, nullable = false)
    private ReferralRequestNotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private ReferralRequestSharedChannel sharedChannel;

    @Column(name = "is_org_admin")
    private boolean isOrgAdmin;

    @ManyToOne
    @JoinColumn(name = "referral_info_request_id")
    private ReferralInfoRequest referralInfoRequest;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Instant getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Instant createdDatetime) {
        this.createdDatetime = createdDatetime;
    }


    public ReferralRequest getReferralRequest() {
        return referralRequest;
    }

    public void setReferralRequest(ReferralRequest referralRequest) {
        this.referralRequest = referralRequest;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Instant getSentDatetime() {
        return sentDatetime;
    }

    public void setSentDatetime(Instant sentDatetime) {
        this.sentDatetime = sentDatetime;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public ReferralRequestNotificationType getType() {
        return type;
    }

    public void setType(ReferralRequestNotificationType type) {
        this.type = type;
    }

    public ReferralRequestSharedChannel getSharedChannel() {
        return sharedChannel;
    }

    public void setSharedChannel(ReferralRequestSharedChannel sharedChannel) {
        this.sharedChannel = sharedChannel;
    }

    public ReferralInfoRequest getReferralInfoRequest() {
        return referralInfoRequest;
    }

    public void setReferralInfoRequest(ReferralInfoRequest referralInfoRequest) {
        this.referralInfoRequest = referralInfoRequest;
    }

    public boolean isOrgAdmin() {
        return isOrgAdmin;
    }

    public void setIsOrgAdmin(boolean orgAdmin) {
        isOrgAdmin = orgAdmin;
    }
}

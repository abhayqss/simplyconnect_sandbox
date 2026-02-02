package com.scnsoft.eldermark.entity.referral;

import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ReferralRequestResponse")
public class ReferralRequestResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "response_datetime")
    private Instant responseDatetime;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private ReferralRequest referralRequest;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "response")
    private ReferralResponse response;

    @ManyToOne
    @JoinColumn(name = "decline_reason_id")
    private ReferralDeclineReason declineReason;

    @Column(name = "comment")
    private String comment;

    @Column(name = "preadmit_date")
    private Instant preadmitDate;

    @Column(name = "service_start_date")
    private Instant serviceStartDate;

    @Column(name = "service_end_date")
    private Instant serviceEndDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getResponseDatetime() {
        return responseDatetime;
    }

    public void setResponseDatetime(Instant responseDatetime) {
        this.responseDatetime = responseDatetime;
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

    public ReferralResponse getResponse() {
        return response;
    }

    public void setResponse(ReferralResponse response) {
        this.response = response;
    }

    public ReferralDeclineReason getDeclineReason() {
        return declineReason;
    }

    public void setDeclineReason(ReferralDeclineReason declineReason) {
        this.declineReason = declineReason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getPreadmitDate() {
        return preadmitDate;
    }

    public void setPreadmitDate(Instant preadmitDate) {
        this.preadmitDate = preadmitDate;
    }

    public Instant getServiceStartDate() {
        return serviceStartDate;
    }

    public void setServiceStartDate(Instant serviceStartDate) {
        this.serviceStartDate = serviceStartDate;
    }

    public Instant getServiceEndDate() {
        return serviceEndDate;
    }

    public void setServiceEndDate(Instant serviceEndDate) {
        this.serviceEndDate = serviceEndDate;
    }
}

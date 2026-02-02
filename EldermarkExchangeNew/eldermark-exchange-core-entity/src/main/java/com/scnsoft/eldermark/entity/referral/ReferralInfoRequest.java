package com.scnsoft.eldermark.entity.referral;

import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ReferralInfoRequest")
public class ReferralInfoRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private ReferralRequest referralRequest;

    @Column(name = "request_id", nullable = false, insertable = false, updatable = false)
    private Long referralRequestId;

    @Column(name = "request_datetime")
    private Instant requestDatetime;

    @Column(name = "subject")
    private String subject;

    @Column(name = "request_message")
    private String requestMessage;

    @Column(name = "requester_name")
    private String requesterName;

    @ManyToOne
    @JoinColumn(name = "requester_employee_id")
    private Employee requesterEmployee;

    @Column(name = "requester_phone_number")
    private String requesterPhoneNumber;

    @Column(name = "response_datetime")
    private Instant responseDatetime;

    @Column(name = "response_message")
    private String responseMessage;

    @ManyToOne
    @JoinColumn(name = "responder_employee_id")
    private Employee responderEmployee;

    @Column(name = "responder_name")
    private String responderName;

    @Column(name = "responder_phone_number")
    private String responderPhoneNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReferralRequest getReferralRequest() {
        return referralRequest;
    }

    public void setReferralRequest(ReferralRequest referralRequest) {
        this.referralRequest = referralRequest;
    }

    public Long getReferralRequestId() {
        return referralRequestId;
    }

    public void setReferralRequestId(Long referralRequestId) {
        this.referralRequestId = referralRequestId;
    }

    public Instant getRequestDatetime() {
        return requestDatetime;
    }

    public void setRequestDatetime(Instant requestDatetime) {
        this.requestDatetime = requestDatetime;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public Employee getRequesterEmployee() {
        return requesterEmployee;
    }

    public void setRequesterEmployee(Employee requesterEmployee) {
        this.requesterEmployee = requesterEmployee;
    }

    public String getRequesterPhoneNumber() {
        return requesterPhoneNumber;
    }

    public void setRequesterPhoneNumber(String requesterPhoneNumber) {
        this.requesterPhoneNumber = requesterPhoneNumber;
    }

    public Instant getResponseDatetime() {
        return responseDatetime;
    }

    public void setResponseDatetime(Instant responseDatetime) {
        this.responseDatetime = responseDatetime;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Employee getResponderEmployee() {
        return responderEmployee;
    }

    public void setResponderEmployee(Employee responderEmployee) {
        this.responderEmployee = responderEmployee;
    }

    public String getResponderName() {
        return responderName;
    }

    public void setResponderName(String responderName) {
        this.responderName = responderName;
    }

    public String getResponderPhoneNumber() {
        return responderPhoneNumber;
    }

    public void setResponderPhoneNumber(String responderPhoneNumber) {
        this.responderPhoneNumber = responderPhoneNumber;
    }
}

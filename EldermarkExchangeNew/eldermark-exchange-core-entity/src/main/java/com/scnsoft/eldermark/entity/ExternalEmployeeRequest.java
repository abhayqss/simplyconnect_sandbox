package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ExternalEmployeeRequest")
public class ExternalEmployeeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "token")
    private String token;

    @Column(name = "created_datetime")
    private Instant createdDateTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "external_employee_inbound_referral_community_id")
    private ExternalEmployeeInboundReferralCommunity employeeCommunity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Instant createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public ExternalEmployeeInboundReferralCommunity getEmployeeCommunity() {
        return employeeCommunity;
    }

    public void setEmployeeCommunity(ExternalEmployeeInboundReferralCommunity employeeCommunity) {
        this.employeeCommunity = employeeCommunity;
    }
}

package com.scnsoft.eldermark.entity.careteam;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;
import java.time.Instant;

@MappedSuperclass
public class CareTeamMemberModifiedBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "care_team_member_id", nullable = false)
    private Long careTeamMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ctm_employee_id", nullable = false, insertable = false, updatable = false)
    private Employee ctmEmployee;

    @Column(name = "ctm_employee_id", nullable = false)
    private Long ctmEmployeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Client client;

    @Column(name = "resident_id", nullable = false)
    private Long clientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "modification_type", nullable = false)
    private CareTeamMemberModificationType modificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_employee_id", insertable = false, updatable = false)
    private Employee performedBy;

    @Column(name = "performed_by_employee_id")
    private Long performedById;

    @Column(name = "date_time", nullable = false)
    private Instant dateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCareTeamMemberId() {
        return careTeamMemberId;
    }

    public void setCareTeamMemberId(Long careTeamMemberId) {
        this.careTeamMemberId = careTeamMemberId;
    }

    public Employee getCtmEmployee() {
        return ctmEmployee;
    }

    public void setCtmEmployee(Employee ctmEmployee) {
        this.ctmEmployee = ctmEmployee;
    }

    public Long getCtmEmployeeId() {
        return ctmEmployeeId;
    }

    public void setCtmEmployeeId(Long ctmEmployeeId) {
        this.ctmEmployeeId = ctmEmployeeId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public CareTeamMemberModificationType getModificationType() {
        return modificationType;
    }

    public void setModificationType(CareTeamMemberModificationType modificationType) {
        this.modificationType = modificationType;
    }

    public Employee getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(Employee performedBy) {
        this.performedBy = performedBy;
    }

    public Long getPerformedById() {
        return performedById;
    }

    public void setPerformedById(Long performedById) {
        this.performedById = performedById;
    }

    public Instant getDateTime() {
        return dateTime;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }
}

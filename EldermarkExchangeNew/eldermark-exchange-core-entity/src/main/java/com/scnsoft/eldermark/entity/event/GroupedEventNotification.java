package com.scnsoft.eldermark.entity.event;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Responsibility;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Immutable
@Table(name = "GroupedSentEventNotification")
public class GroupedEventNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Event event;

    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    @ManyToOne
    private Employee employee;

    @JoinColumn(name = "care_team_role_id", referencedColumnName = "id")
    @ManyToOne
    private CareTeamRole careTeamRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "responsibility", length = 50, nullable = false)
    private Responsibility responsibility;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public CareTeamRole getCareTeamRole() {
        return careTeamRole;
    }

    public void setCareTeamRole(CareTeamRole careTeamRole) {
        this.careTeamRole = careTeamRole;
    }

    public Responsibility getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(Responsibility responsibility) {
        this.responsibility = responsibility;
    }
}

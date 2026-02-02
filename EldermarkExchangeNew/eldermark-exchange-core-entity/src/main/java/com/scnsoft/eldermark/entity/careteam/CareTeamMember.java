package com.scnsoft.eldermark.entity.careteam;

import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@Entity
@Table(name = "CareTeamMember")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CareTeamMember implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "description")
    private String description;

    //TODO changing to optional=false make list query with  cross join instead outer join in 2 relations:
    //careTeamMember -> employee, employee -> community
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.DETACH)
    private Employee employee;

    @Column(name = "employee_id", insertable = false, updatable = false)
    private Long employeeId;

    @JoinColumn(name = "created_by_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private Employee createdBy;

    @Column(name = "created_by_id")
    private Long createdById;

    @JoinColumn(name = "care_team_role_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private CareTeamRole careTeamRole;
   
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "careTeamMember", orphanRemoval = true)
    private List<CareTeamMemberNotificationPreferences> notificationPreferences;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public CareTeamRole getCareTeamRole() {
        return careTeamRole;
    }

    public void setCareTeamRole(CareTeamRole careTeamRole) {
        this.careTeamRole = careTeamRole;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public Employee getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Employee createdBy) {
        this.createdBy = createdBy;
    }

    public List<CareTeamMemberNotificationPreferences> getNotificationPreferences() {
        return notificationPreferences;
    }

    public void setNotificationPreferences(List<CareTeamMemberNotificationPreferences> notificationPreferences) {
        this.notificationPreferences = notificationPreferences;
    }
}

package com.scnsoft.eldermark.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 * @author knetkachev
 * @author phomal
 * @author pzhurba
 */
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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "careTeamMember")
    private List<CareTeamMemberNotificationPreferences> careTeamMemberNotificationPreferencesList;

    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, cascade = CascadeType.DETACH)
    private Employee employee;

    @JoinColumn(name = "created_by_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private Employee createdBy;

    @Column(name = "created_by_id")
    private Long createdById;

    @JoinColumn(name = "care_team_role_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private CareTeamRole careTeamRole;

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

    public List<CareTeamMemberNotificationPreferences> getCareTeamMemberNotificationPreferencesList() {
        return careTeamMemberNotificationPreferencesList;
    }

    public void setCareTeamMemberNotificationPreferencesList(List<CareTeamMemberNotificationPreferences> careTeamMemberNotificationPreferencesList) {
        this.careTeamMemberNotificationPreferencesList = careTeamMemberNotificationPreferencesList;
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
}

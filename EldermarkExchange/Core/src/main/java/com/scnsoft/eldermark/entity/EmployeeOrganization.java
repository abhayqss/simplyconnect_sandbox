package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Employee_Organization",
       uniqueConstraints = @UniqueConstraint(columnNames = {"legacy_id", "database_id"}))
public class EmployeeOrganization extends LegacyIdAwareEntity {

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
	private Employee employee;

    @OneToMany
    @JoinTable(name = "Employee_Organization_Role",
            joinColumns = @JoinColumn( name="employee_organization_id"),
            inverseJoinColumns = @JoinColumn( name="role_id") )
    private List<Role> roles;

    @OneToMany
    @JoinTable(name = "Employee_Organization_Group",
            joinColumns = @JoinColumn( name="employee_organization_id"),
            inverseJoinColumns = @JoinColumn( name="group_id") )
    private List<Group> groups;

    public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
}

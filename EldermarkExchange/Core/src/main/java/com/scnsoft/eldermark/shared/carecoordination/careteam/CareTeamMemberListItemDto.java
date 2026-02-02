package com.scnsoft.eldermark.shared.carecoordination.careteam;

import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;

/**
 * Created by pzhurba on 20-Oct-15.
 */
public class CareTeamMemberListItemDto {
    private Long id;
    private KeyValueDto employee;
    private KeyValueDto role;
    private String description;
    private boolean editable = false;
    private boolean deletable = false;
    private Boolean createdAutomatically;
    private boolean roleEditable = false;
    private Long employeeDatabaseId;
    private String employeeDatabaseName;
    private Long residentDatabaseId;
    private String residentDatabaseName;
    private Long residentId;
    private String nucleusUserId;

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

    public KeyValueDto getEmployee() {
        return employee;
    }

    public void setEmployee(KeyValueDto employee) {
        this.employee = employee;
    }

    public KeyValueDto getRole() {
        return role;
    }

    public void setRole(KeyValueDto role) {
        this.role = role;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public Boolean getCreatedAutomatically() {
        return createdAutomatically;
    }

    public void setCreatedAutomatically(Boolean createdAutomatically) {
        this.createdAutomatically = createdAutomatically;
    }

    public boolean isRoleEditable() {
        return roleEditable;
    }

    public void setRoleEditable(boolean roleEditable) {
        this.roleEditable = roleEditable;
    }

    public Long getEmployeeDatabaseId() {
        return employeeDatabaseId;
    }

    public void setEmployeeDatabaseId(Long employeeDatabaseId) {
        this.employeeDatabaseId = employeeDatabaseId;
    }

    public String getEmployeeDatabaseName() {
        return employeeDatabaseName;
    }

    public void setEmployeeDatabaseName(String employeeDatabaseName) {
        this.employeeDatabaseName = employeeDatabaseName;
    }

    public Long getResidentDatabaseId() {
        return residentDatabaseId;
    }

    public void setResidentDatabaseId(Long residentDatabaseId) {
        this.residentDatabaseId = residentDatabaseId;
    }

    public String getResidentDatabaseName() {
        return residentDatabaseName;
    }

    public void setResidentDatabaseName(String residentDatabaseName) {
        this.residentDatabaseName = residentDatabaseName;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    /**
     * Nucleus user ID associated with Employee
     */
    public String getNucleusUserId() {
        return nucleusUserId;
    }

    public void setNucleusUserId(String nucleusUserId) {
        this.nucleusUserId = nucleusUserId;
    }
}

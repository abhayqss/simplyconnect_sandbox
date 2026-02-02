package com.scnsoft.eldermark.entity.document.folder;

import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;

@Entity
@Table(name = "DocumentFolderPermission")
public class DocumentFolderPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "employee_id", nullable = false, insertable = false, updatable = false)
    private Long employeeId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "permission_level_id", nullable = false)
    private DocumentFolderPermissionLevel permissionLevel;

    @Column(name = "folder_id", insertable = false, updatable = false)
    private Long folderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private DocumentFolder folder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        setEmployeeId(employee.getId());
    }

    public DocumentFolderPermissionLevel getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(DocumentFolderPermissionLevel permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public DocumentFolder getFolder() {
        return folder;
    }

    public void setFolder(DocumentFolder folder) {
        this.folder = folder;
        setFolderId(folder.getId());
    }
}

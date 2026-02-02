package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.beans.audit.AuditLogAction;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.Document;
import com.scnsoft.eldermark.entity.prospect.Prospect;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "AuditLog")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "date", nullable = false)
    private Instant date;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private AuditLogAction action;

    @Column(name = "remote_address")
    private String remoteAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, insertable = false, updatable = false)
    private Employee employee;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @ManyToMany
    @JoinTable(name = "AuditLog_Residents",
            joinColumns = @JoinColumn(name = "audit_log_id"),
            inverseJoinColumns = @JoinColumn(name = "resident_id", insertable = false, updatable = false))
    private List<Client> clients;

    @ElementCollection
    @CollectionTable(name = "AuditLog_Residents", joinColumns = @JoinColumn(name = "audit_log_id"))
    @Column(name = "resident_id")
    private Set<Long> clientIds;

    @ManyToMany
    @JoinTable(name = "AuditLog_Prospects",
            joinColumns = @JoinColumn(name = "audit_log_id"),
            inverseJoinColumns = @JoinColumn(name = "prospect_id", insertable = false, updatable = false))
    private List<Prospect> prospects;

    @ElementCollection
    @CollectionTable(name = "AuditLog_Prospects", joinColumns = @JoinColumn(name = "audit_log_id"))
    @Column(name = "prospect_id")
    private Set<Long> prospectIds;

    @ManyToMany
    @JoinTable(name = "AuditLog_Documents",
            joinColumns = @JoinColumn(name = "audit_log_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id"))
    private List<Document> documents;

    @ElementCollection
    @CollectionTable(name = "AuditLog_Documents", joinColumns = @JoinColumn(name = "audit_log_id"))
    @Column(name = "document_id")
    private Set<Long> documentIds;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "audit_log_relation_id")
    private AuditLogRelation auditLogRelation;

    @Column(name = "audit_log_relation_id", nullable = false, insertable = false, updatable = false)
    private Long auditLogRelationId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "audit_log_search_filter_id")
    private AuditLogSearchFilter auditLogSearchFilter;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "AuditLog_Database",
            joinColumns = {@JoinColumn(name = "audit_log_id")},
            inverseJoinColumns = {@JoinColumn(name = "database_id", insertable = false, updatable = false)})
    private List<Organization> organizations;

    @ElementCollection
    @CollectionTable(name = "AuditLog_Database", joinColumns = @JoinColumn(name = "audit_log_id"))
    @Column(name = "database_id")
    private Set<Long> organizationIds;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "AuditLog_Organization",
            joinColumns = {@JoinColumn(name = "audit_log_id")},
            inverseJoinColumns = {@JoinColumn(name = "organization_id", insertable = false, updatable = false)})
    private List<Community> communities;

    @ElementCollection
    @CollectionTable(name = "AuditLog_Organization", joinColumns = @JoinColumn(name = "audit_log_id"))
    @Column(name = "organization_id")
    private Set<Long> communityIds;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "auditLog")
    private AuditLogFirstClient firstClient;

    //todo switch to String platform instead?
    @Column(name = "is_mobile", nullable = false)
    private boolean isMobile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public AuditLogAction getAction() {
        return action;
    }

    public void setAction(AuditLogAction action) {
        this.action = action;
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

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public Set<Long> getClientIds() {
        return clientIds;
    }

    public void setClientIds(Set<Long> clientIds) {
        this.clientIds = clientIds;
    }

    public List<Prospect> getProspects() {
        return prospects;
    }

    public void setProspects(List<Prospect> prospects) {
        this.prospects = prospects;
    }

    public Set<Long> getProspectIds() {
        return prospectIds;
    }

    public void setProspectIds(Set<Long> prospectIds) {
        this.prospectIds = prospectIds;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public Set<Long> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(Set<Long> documentIds) {
        this.documentIds = documentIds;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public AuditLogRelation getAuditLogRelation() {
        return auditLogRelation;
    }

    public void setAuditLogRelation(AuditLogRelation auditLogRelation) {
        this.auditLogRelation = auditLogRelation;
    }

    public Long getAuditLogRelationId() {
        return auditLogRelationId;
    }

    public void setAuditLogRelationId(Long auditLogRelationId) {
        this.auditLogRelationId = auditLogRelationId;
    }

    public AuditLogSearchFilter getAuditLogSearchFilter() {
        return auditLogSearchFilter;
    }

    public void setAuditLogSearchFilter(AuditLogSearchFilter auditLogSearchFilter) {
        this.auditLogSearchFilter = auditLogSearchFilter;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
    }

    public Set<Long> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(Set<Long> organizationIds) {
        this.organizationIds = organizationIds;
    }

    public List<Community> getCommunities() {
        return communities;
    }

    public void setCommunities(List<Community> communities) {
        this.communities = communities;
    }

    public Set<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(Set<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public AuditLogFirstClient getFirstClient() {
        return firstClient;
    }

    public void setFirstClient(AuditLogFirstClient firstClientName) {
        this.firstClient = firstClientName;
    }

    public boolean isMobile() {
        return isMobile;
    }

    public void setMobile(boolean mobile) {
        isMobile = mobile;
    }
}
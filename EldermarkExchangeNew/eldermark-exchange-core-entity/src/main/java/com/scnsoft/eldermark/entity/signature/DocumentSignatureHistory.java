package com.scnsoft.eldermark.entity.signature;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.document.Document;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Immutable
@Table(name = "DocumentSignatureHistoryView")
public class DocumentSignatureHistory {

    @EmbeddedId
    private DocumentSignatureHistory.Id id;

    @Column(name = "action", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private DocumentSignatureHistoryAction action;

    @Column(name = "action_title", insertable = false, updatable = false)
    private String actionTitle;

    @Column(name = "document_id", insertable = false, updatable = false)
    private Long documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", insertable = false, updatable = false)
    private Document document;

    @Column(name = "actor_first_name", insertable = false, updatable = false)
    @Nationalized
    private String actorFirstName;

    @Column(name = "actor_last_name", insertable = false, updatable = false)
    @Nationalized
    private String actorLastName;

    @Column(name = "actor_role_id", insertable = false, updatable = false)
    private Long actorRoleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_role_id", insertable = false, updatable = false)
    private CareTeamRole actorRole;

    @Column(name = "employee_id", insertable = false, updatable = false)
    private Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;

    @Column(name = "client_id", insertable = false, updatable = false)
    private Long clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    private Client client;

    @Column(name = "date", insertable = false, updatable = false)
    private Instant date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", insertable = false, updatable = false)
    private DocumentSignatureRequest request;

    @Column(name = "request_id", insertable = false, updatable = false)
    private Long requestId;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public DocumentSignatureHistoryAction getAction() {
        return action;
    }

    public void setAction(DocumentSignatureHistoryAction action) {
        this.action = action;
    }

    public String getActionTitle() {
        return actionTitle;
    }

    public void setActionTitle(String actionTitle) {
        this.actionTitle = actionTitle;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getActorFirstName() {
        return actorFirstName;
    }

    public void setActorFirstName(String actorFirstName) {
        this.actorFirstName = actorFirstName;
    }

    public String getActorLastName() {
        return actorLastName;
    }

    public void setActorLastName(String actorLastName) {
        this.actorLastName = actorLastName;
    }

    public Long getActorRoleId() {
        return actorRoleId;
    }

    public void setActorRoleId(Long actorRoleId) {
        this.actorRoleId = actorRoleId;
    }

    public CareTeamRole getActorRole() {
        return actorRole;
    }

    public void setActorRole(CareTeamRole actorRole) {
        this.actorRole = actorRole;
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
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public DocumentSignatureRequest getRequest() {
        return request;
    }

    public void setRequest(DocumentSignatureRequest request) {
        this.request = request;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "request_id", insertable = false, updatable = false)
        private Long requestId;

        @Column(name = "action", insertable = false, updatable = false)
        @Enumerated(EnumType.STRING)
        private DocumentSignatureHistoryAction action;

        public Long getRequestId() {
            return requestId;
        }

        public void setRequestId(Long requestId) {
            this.requestId = requestId;
        }

        public DocumentSignatureHistoryAction getAction() {
            return action;
        }

        public void setAction(DocumentSignatureHistoryAction action) {
            this.action = action;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return Objects.equals(requestId, id.requestId) && action == id.action;
        }

        @Override
        public int hashCode() {
            return Objects.hash(requestId, action);
        }
    }

}

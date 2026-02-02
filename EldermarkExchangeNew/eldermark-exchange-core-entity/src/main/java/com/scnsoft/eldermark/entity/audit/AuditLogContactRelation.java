package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_Employee")
public class AuditLogContactRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "contact_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Employee contact;

    @Column(name = "contact_id", nullable = false)
    private Long contactId;

    public Employee getContact() {
        return contact;
    }

    public void setContact(Employee contact) {
        this.contact = contact;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(contactId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.CONTACT;
    }
}

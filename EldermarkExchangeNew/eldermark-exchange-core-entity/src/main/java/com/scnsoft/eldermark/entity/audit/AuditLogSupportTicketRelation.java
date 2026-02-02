package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.SupportTicket;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_SupportTicket")
public class AuditLogSupportTicketRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "support_ticket_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private SupportTicket supportTicket;

    @Column(name = "support_ticket_id", nullable = false)
    private Long supportTicketId;

    public SupportTicket getSupportTicket() {
        return supportTicket;
    }

    public void setSupportTicket(SupportTicket supportTicket) {
        this.supportTicket = supportTicket;
    }

    public Long getSupportTicketId() {
        return supportTicketId;
    }

    public void setSupportTicketId(Long supportTicketId) {
        this.supportTicketId = supportTicketId;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(supportTicketId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.SUPPORT_TICKET;
    }
}

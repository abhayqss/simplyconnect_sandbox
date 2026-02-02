package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.lab.LabResearchOrder;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_LabResearchOrder")
public class AuditLogLabResearchOrderRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "lab_order_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private LabResearchOrder labResearchOrder;

    @Column(name = "lab_order_id", nullable = false)
    private Long labResearchOrderId;

    public LabResearchOrder getLabResearchOrder() {
        return labResearchOrder;
    }

    public void setLabResearchOrder(LabResearchOrder labResearchOrder) {
        this.labResearchOrder = labResearchOrder;
    }

    public Long getLabResearchOrderId() {
        return labResearchOrderId;
    }

    public void setLabResearchOrderId(Long labResearchOrderId) {
        this.labResearchOrderId = labResearchOrderId;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(labResearchOrderId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.LAB_RESEARCH_ORDER;
    }
}

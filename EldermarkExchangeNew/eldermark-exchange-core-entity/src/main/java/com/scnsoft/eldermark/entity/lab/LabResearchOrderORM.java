package com.scnsoft.eldermark.entity.lab;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "LabResearchOrderORM")
public class LabResearchOrderORM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "lab_research_order_id")
    private LabResearchOrder order;

    @Lob
    @Column(name = "orm_raw")
    private String ormRaw;

    @Column(name = "sent_datetime")
    private Instant sentDatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LabResearchOrder getOrder() {
        return order;
    }

    public void setOrder(LabResearchOrder order) {
        this.order = order;
    }

    public String getOrmRaw() {
        return ormRaw;
    }

    public void setOrmRaw(String ormRaw) {
        this.ormRaw = ormRaw;
    }

    public Instant getSentDatetime() {
        return sentDatetime;
    }

    public void setSentDatetime(Instant sentDatetime) {
        this.sentDatetime = sentDatetime;
    }
}

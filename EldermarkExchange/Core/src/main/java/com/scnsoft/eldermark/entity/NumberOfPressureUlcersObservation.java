package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class NumberOfPressureUlcersObservation extends BasicEntity {
    @Column(name = "effective_time")
    private Date effectiveTime;

    private Integer value;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne
    @JoinColumn(name = "observation_value_id")
    private CcdCode observationValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functional_status_id", nullable = false)
    private FunctionalStatus functionalStatus;

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public CcdCode getObservationValue() {
        return observationValue;
    }

    public void setObservationValue(CcdCode observationValue) {
        this.observationValue = observationValue;
    }
}

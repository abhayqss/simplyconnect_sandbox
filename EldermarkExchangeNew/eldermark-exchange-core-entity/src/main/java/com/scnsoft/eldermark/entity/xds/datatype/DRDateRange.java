package com.scnsoft.eldermark.entity.xds.datatype;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "DR_DateRange")
public class DRDateRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "range_start_datetime")
    private Instant rangeStartDatetime;

    @Column(name = "range_end_datetime")
    private Instant rangeEndDatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getRangeStartDatetime() {
        return rangeStartDatetime;
    }

    public void setRangeStartDatetime(Instant rangeStartDatetime) {
        this.rangeStartDatetime = rangeStartDatetime;
    }

    public Instant getRangeEndDatetime() {
        return rangeEndDatetime;
    }

    public void setRangeEndDatetime(Instant rangeEndDatetime) {
        this.rangeEndDatetime = rangeEndDatetime;
    }
}

package com.scnsoft.eldermark.entity.document.ccd;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.basic.LegacyTableAwareEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
@Table(name = "Indication")
public class Indication extends LegacyTableAwareEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "code_id")
    private CcdCode code;

    @Column(name = "effective_time_low")
    private Date timeLow;

    @Column(name = "effective_time_high")
    private Date timeHigh;

    @ManyToOne
    @JoinColumn(name = "value_code_id")
    private CcdCode value;

    public Date getTimeLow() {
        return timeLow;
    }

    public void setTimeLow(Date timeLow) {
        this.timeLow = timeLow;
    }

    public Date getTimeHigh() {
        return timeHigh;
    }

    public void setTimeHigh(Date timeHigh) {
        this.timeHigh = timeHigh;
    }

    public CcdCode getValue() {
        return value;
    }

    public void setValue(CcdCode value) {
        this.value = value;
    }

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }
}

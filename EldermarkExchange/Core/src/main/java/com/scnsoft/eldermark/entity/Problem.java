package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import org.apache.commons.lang3.time.DateUtils;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
public class Problem extends LegacyIdAwareEntity implements AdtMessageAwareEntity {

    @Column(name = "status_code", length = 20)
    private String statusCode;

    @Column(name = "effective_time_low")
    private Date timeLow;

    @Column(name = "effective_time_high")
    private Date timeHigh;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProblemObservation> problemObservations;

    @Column(name = "rank")
    private Integer rank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long residentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adt_msg_id")
    private AdtMessage adtMessage;

    /**
     * @return Problem Act Status Code stored in DB or deduced from low / high time
     */
    public String getStatusCode() {
        if (statusCode != null) {
            return statusCode;
        }

        Date todayDate = DateUtils.truncate(new Date(), Calendar.DATE);
        if (getTimeHigh() == null && getTimeLow() != null && getTimeLow().compareTo(todayDate) <= 0) {
            statusCode = ProblemActStatusCode.ACTIVE.getCode();
        } else if (getTimeHigh() != null && getTimeHigh().compareTo(todayDate) <= 0) {
            statusCode = ProblemActStatusCode.COMPLETED.getCode();
        }

        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

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

    public List<ProblemObservation> getProblemObservations() {
        return problemObservations;
    }

    public void setProblemObservations(List<ProblemObservation> problemObservations) {
        this.problemObservations = problemObservations;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    @Override
    public AdtMessage getAdtMessage() {
        return adtMessage;
    }

    @Override
    public void setAdtMessage(AdtMessage adtMessage) {
        this.adtMessage = adtMessage;
    }
}

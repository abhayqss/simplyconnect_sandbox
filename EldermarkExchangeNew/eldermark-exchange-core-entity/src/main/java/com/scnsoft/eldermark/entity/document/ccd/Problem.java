package com.scnsoft.eldermark.entity.document.ccd;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;
import com.scnsoft.eldermark.entity.document.ccd.codes.ProblemActStatusCode;
import org.apache.commons.lang3.time.DateUtils;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
public class Problem extends LegacyIdAwareEntity {
    private static final long serialVersionUID = 1L;

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
    private Client client;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long clientId;


    @Column(name = "dg1_id")
    private Long dg1Id;

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getDg1Id() {
        return dg1Id;
    }

    public void setDg1Id(Long dg1Id) {
        this.dg1Id = dg1Id;
    }
}

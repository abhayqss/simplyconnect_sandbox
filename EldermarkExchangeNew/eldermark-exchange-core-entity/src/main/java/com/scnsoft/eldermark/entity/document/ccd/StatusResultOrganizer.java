package com.scnsoft.eldermark.entity.document.ccd;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
public class StatusResultOrganizer extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn
    private CcdCode code;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "StatusResultOrganizer_StatusResultObservation",
            joinColumns = @JoinColumn(name = "status_result_organizer_id"),
            inverseJoinColumns = @JoinColumn(name = "status_result_observation_id"))
    private List<StatusResultObservation> statusResultObservations;

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }

    public List<StatusResultObservation> getStatusResultObservations() {
        return statusResultObservations;
    }

    public void setStatusResultObservations(List<StatusResultObservation> statusResultObservations) {
        this.statusResultObservations = statusResultObservations;
    }
}

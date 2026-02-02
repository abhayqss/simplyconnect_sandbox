package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class StatusResultOrganizer extends BasicEntity {
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

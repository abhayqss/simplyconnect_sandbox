package com.scnsoft.eldermark.entity.client;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.BasicEntity;

import javax.persistence.*;
import java.time.Instant;

@Table(name = "ResidentNotes")
@Entity
public class ClientNotes extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name="resident_id")
    public Client client;

    @Column(name = "note", columnDefinition="text")
    private String note;

    @Column(name = "note_start_date")
    private Instant startDate;

    @Column(name = "note_end_date")
    private Instant endDate;

    public Client getClient() {
        return client;
    }

    public void setClient(Client resident) {
        this.client = resident;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        Instant now = Instant.now();
        return startDate != null && now.isAfter(startDate) && endDate == null;
    }
}

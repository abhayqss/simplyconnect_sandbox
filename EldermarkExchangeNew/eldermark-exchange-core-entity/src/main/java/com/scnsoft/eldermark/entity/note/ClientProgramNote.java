package com.scnsoft.eldermark.entity.note;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "ClientProgramNote")
public class ClientProgramNote extends Note {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_program_note_type_id", referencedColumnName = "id", nullable = false)
    private ClientProgramNoteType clientProgramNoteType;

    @Column(name = "service_provider", nullable = false)
    private String serviceProvider;

    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    public ClientProgramNoteType getClientProgramNoteType() {
        return clientProgramNoteType;
    }

    public void setClientProgramNoteType(ClientProgramNoteType clientProgramNoteType) {
        this.clientProgramNoteType = clientProgramNoteType;
    }

    public String getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider;
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
}

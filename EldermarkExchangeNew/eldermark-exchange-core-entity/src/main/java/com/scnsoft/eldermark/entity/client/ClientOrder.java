package com.scnsoft.eldermark.entity.client;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.BasicEntity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ResidentOrder")
public class ClientOrder extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    public Client client;

    @Column(name = "order_name", columnDefinition = "text")
    private String name;

    @Column(name = "order_start_date")
    private Instant startDate;

    @Column(name = "order_end_date")
    private Instant endDate;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

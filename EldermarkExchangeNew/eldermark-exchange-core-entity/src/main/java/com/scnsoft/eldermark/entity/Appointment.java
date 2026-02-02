package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.community.Community;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table
public class Appointment implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    
    @Column(name = "created_date")
    private Instant date;
    
    @Column(name="database_id")
    private Long organizationId;
    
    @OneToOne
    @JoinColumn(name = "organization_id")
    private Community community;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
    
}

package com.scnsoft.eldermark.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * @author phomal
 * @author pzhurba
 */
@Entity
@Table(name = "EventTreatingHospital")
public class EventTreatingHospital implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic(optional = false)
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "phone", length = 16)
    private String phone;
    @JoinColumn(name = "event_address_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.ALL)
    private EventAddress eventAddress;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public EventAddress getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(EventAddress eventAddress) {
        this.eventAddress = eventAddress;
    }
}

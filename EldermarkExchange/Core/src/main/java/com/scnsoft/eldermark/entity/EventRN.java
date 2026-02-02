package com.scnsoft.eldermark.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * @author phomal
 * @author pzhurba
 */
@Entity
@Table(name = "EventRN")
public class EventRN implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic(optional = false)
    @Column(name = "first_name", length = 128, nullable = false)
    private String firstName;
    @Basic(optional = false)
    @Column(name = "last_name", length = 128, nullable = false)
    private String lastName;
    @JoinColumn(name = "event_address_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.ALL)
    private EventAddress eventAddress;

    public EventRN() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public EventAddress getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(EventAddress eventAddress) {
        this.eventAddress = eventAddress;
    }
}

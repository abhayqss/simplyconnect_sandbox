package com.scnsoft.eldermark.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * @author phomal
 * @author pzhurba
 */
@Entity
@Table(name = "EventAddress")
public class EventAddress implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic(optional = false)
    @Column(name = "street", nullable = false)
    private String street;
    @Basic(optional = false)
    @Column(name = "city", length = 128, nullable = false)
    private String city;
    @JoinColumn(name = "state_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private State state;
    @Basic(optional = false)
    @Column(name = "zip", length = 10, nullable = false)
    private String zip;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}

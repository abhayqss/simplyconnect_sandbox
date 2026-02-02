package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "DeliveryLocation")
public class ServiceDeliveryLocation extends BasicEntity {

    @ManyToOne
    @JoinColumn
    private CcdCode code;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "DeliveryLocation_OrganizationAddress",
            joinColumns = @JoinColumn(name = "delivery_location_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id"))
    private List<OrganizationAddress> addresses;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "DeliveryLocation_OrganizationTelecom",
            joinColumns = @JoinColumn(name = "delivery_location_id"),
            inverseJoinColumns = @JoinColumn(name = "telecom_id"))
    private List<OrganizationTelecom> telecoms;

    @Column
    private String name;

    @Column
    private String description;

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }

    public List<OrganizationAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<OrganizationAddress> addresses) {
        this.addresses = addresses;
    }

    public List<OrganizationTelecom> getTelecoms() {
        return telecoms;
    }

    public void setTelecoms(List<OrganizationTelecom> telecoms) {
        this.telecoms = telecoms;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

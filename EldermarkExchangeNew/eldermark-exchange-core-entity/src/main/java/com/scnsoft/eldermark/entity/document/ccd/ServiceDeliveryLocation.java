package com.scnsoft.eldermark.entity.document.ccd;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.community.CommunityAddress;
import com.scnsoft.eldermark.entity.community.CommunityTelecom;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
@Table(name = "DeliveryLocation")
public class ServiceDeliveryLocation extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn
    private CcdCode code;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "DeliveryLocation_OrganizationAddress",
            joinColumns = @JoinColumn(name = "delivery_location_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id"))
    private List<CommunityAddress> addresses;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "DeliveryLocation_OrganizationTelecom",
            joinColumns = @JoinColumn(name = "delivery_location_id"),
            inverseJoinColumns = @JoinColumn(name = "telecom_id"))
    private List<CommunityTelecom> telecoms;

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

    public List<CommunityAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<CommunityAddress> addresses) {
        this.addresses = addresses;
    }

    public List<CommunityTelecom> getTelecoms() {
        return telecoms;
    }

    public void setTelecoms(List<CommunityTelecom> telecoms) {
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

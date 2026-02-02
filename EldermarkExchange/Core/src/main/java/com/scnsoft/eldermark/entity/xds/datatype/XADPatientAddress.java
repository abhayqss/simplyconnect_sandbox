package com.scnsoft.eldermark.entity.xds.datatype;

import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0190AddressType;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0399CountryCode;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0465NameAddressRepresentation;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "XAD_PatientAddress")
public class XADPatientAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "address_type_id")
    private IDCodedValueForHL7Tables<HL7CodeTable0190AddressType> addressType;

    //@Nationalized
    @Column(name = "street_address")
    private String streetAddress;

    //@Nationalized
    @Column(name = "other_designation")
    private String otherDesignation;

    //@Nationalized
    @Column(name = "city")
    private String city;

    //@Nationalized
    @Column(name = "state")
    private String state;

    //@Nationalized
    @Column(name = "county")
    private String county;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "country_id")
    private IDCodedValueForHL7Tables<HL7CodeTable0399CountryCode> country;

    //@Nationalized
    @Column(name = "zip")
    private String zip;

    @Column(name = "other_geographic_designation")
    private String otherGeographicDesignation;

    @Column(name = "census_tract")
    private String censusTract;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "address_representation_code_id")
    private IDCodedValueForHL7Tables<HL7CodeTable0465NameAddressRepresentation> addressRepresentationCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IDCodedValueForHL7Tables<HL7CodeTable0190AddressType> getAddressType() {
        return addressType;
    }

    public void setAddressType(IDCodedValueForHL7Tables<HL7CodeTable0190AddressType> addressType) {
        this.addressType = addressType;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getOtherDesignation() {
        return otherDesignation;
    }

    public void setOtherDesignation(String otherDesignation) {
        this.otherDesignation = otherDesignation;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public IDCodedValueForHL7Tables<HL7CodeTable0399CountryCode> getCountry() {
        return country;
    }

    public void setCountry(IDCodedValueForHL7Tables<HL7CodeTable0399CountryCode> country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getOtherGeographicDesignation() {
        return otherGeographicDesignation;
    }

    public void setOtherGeographicDesignation(String otherGeographicDesignation) {
        this.otherGeographicDesignation = otherGeographicDesignation;
    }

    public String getCensusTract() {
        return censusTract;
    }

    public void setCensusTract(String censusTract) {
        this.censusTract = censusTract;
    }

    public IDCodedValueForHL7Tables<HL7CodeTable0465NameAddressRepresentation> getAddressRepresentationCode() {
        return addressRepresentationCode;
    }

    public void setAddressRepresentationCode(IDCodedValueForHL7Tables<HL7CodeTable0465NameAddressRepresentation> addressRepresentationCode) {
        this.addressRepresentationCode = addressRepresentationCode;
    }
}

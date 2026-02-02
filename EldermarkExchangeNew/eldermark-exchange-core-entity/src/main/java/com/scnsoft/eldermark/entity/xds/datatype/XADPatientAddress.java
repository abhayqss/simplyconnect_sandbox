package com.scnsoft.eldermark.entity.xds.datatype;

import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0190AddressType;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0399CountryCode;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0465NameAddressRepresentation;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;

@Entity
@Table(name = "XAD_PatientAddress")
public class XADPatientAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "street_address", columnDefinition = "nvarchar(255)")
    @Nationalized
    private String streetAddress;

    @Column(name = "other_designation", columnDefinition = "nvarchar(255)")
    @Nationalized
    private String otherDesignation;

    @Column(name = "city", columnDefinition = "nvarchar(50)")
    @Nationalized
    private String city;

    @Column(name = "state", columnDefinition = "nvarchar(30)")
    @Nationalized
    private String state;

    @Column(name = "zip", columnDefinition = "nvarchar(10)")
    @Nationalized
    private String zip;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "country_id")
    private IDCodedValueForHL7Tables<HL7CodeTable0399CountryCode> country;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_type_id")
    private IDCodedValueForHL7Tables<HL7CodeTable0190AddressType> addressType;

    @Column(name = "other_geographic_designation")
    private String otherGeographicDesignation;

    @Column(name = "county", columnDefinition = "nvarchar(30)")
    @Nationalized
    private String county;

    @Column(name = "census_tract")
    private String censusTract;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_representation_code_id")
    private IDCodedValueForHL7Tables<HL7CodeTable0465NameAddressRepresentation> addressRepresentationCode;

    public XADPatientAddress() {
    }

    public XADPatientAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public XADPatientAddress(String streetAddress, String otherDesignation, String city, String state, String zip,
                             IDCodedValueForHL7Tables<HL7CodeTable0399CountryCode> country,
                             IDCodedValueForHL7Tables<HL7CodeTable0190AddressType> addressType,
                             String otherGeographicDesignation, String county, String censusTract,
                             IDCodedValueForHL7Tables<HL7CodeTable0465NameAddressRepresentation> addressRepresentationCode) {
        this.streetAddress = streetAddress;
        this.otherDesignation = otherDesignation;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
        this.addressType = addressType;
        this.otherGeographicDesignation = otherGeographicDesignation;
        this.county = county;
        this.censusTract = censusTract;
        this.addressRepresentationCode = addressRepresentationCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public IDCodedValueForHL7Tables<HL7CodeTable0190AddressType> getAddressType() {
        return addressType;
    }

    public void setAddressType(IDCodedValueForHL7Tables<HL7CodeTable0190AddressType> addressType) {
        this.addressType = addressType;
    }

    public String getOtherGeographicDesignation() {
        return otherGeographicDesignation;
    }

    public void setOtherGeographicDesignation(String otherGeographicDesignation) {
        this.otherGeographicDesignation = otherGeographicDesignation;
    }

    public IDCodedValueForHL7Tables<HL7CodeTable0399CountryCode> getCountry() {
        return country;
    }

    public void setCountry(IDCodedValueForHL7Tables<HL7CodeTable0399CountryCode> country) {
        this.country = country;
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

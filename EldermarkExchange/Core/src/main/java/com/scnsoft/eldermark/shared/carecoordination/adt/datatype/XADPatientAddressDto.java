package com.scnsoft.eldermark.shared.carecoordination.adt.datatype;

import org.apache.commons.lang.StringUtils;

public class XADPatientAddressDto {
    private String addressType;
    private String streetAddress;
    private String otherDesignation;
    private String city;
    private String state;
    private String county;
    private String country;
    private String zip;
    private String otherGeographicDesignation;
    private String censusTract;
    private String addressRepresentationCode;

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
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

    public String getAddressRepresentationCode() {
        return addressRepresentationCode;
    }

    public void setAddressRepresentationCode(String addressRepresentationCode) {
        this.addressRepresentationCode = addressRepresentationCode;
    }

    @Override
    public String toString(){
        StringBuilder address = new StringBuilder();
        if(StringUtils.isNotEmpty(zip)){
            address.append(zip).append(", ");
        }
        if(StringUtils.isNotEmpty(country)){
            address.append(country).append(", ");
        }
        if(StringUtils.isNotEmpty(city)){
            address.append(city).append(", ");
        }
        if(StringUtils.isNotEmpty(county)){
            address.append(county).append(", ");
        }
        if(StringUtils.isNotEmpty(state)){
            address.append(state).append(", ");
        }
        if(StringUtils.isNotEmpty(streetAddress)){
            address.append(streetAddress).append(", ");
        }
        if(StringUtils.isNotEmpty(otherGeographicDesignation)){
            address.append(otherGeographicDesignation).append(", ");
        }
        if(StringUtils.isNotEmpty(censusTract)){
            address.append(censusTract).append(", ");
        }
        if(StringUtils.isNotEmpty(addressRepresentationCode)){
            address.append(addressRepresentationCode).append(", ");
        }
        if (address.length() > 0) {
            return address.substring(0, address.length() - 2);
        }
        return StringUtils.EMPTY;
    }
}

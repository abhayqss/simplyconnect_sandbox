package org.openhealthtools.openxds.entity.datatype;

import javax.persistence.*;

@Entity
@Table(name = "XTN_PhoneNumber")
//todo add more fields according to spec
//http://hl7-definition.caristix.com:9010/HL7%20v2.3/triggerEvent/Default.aspx?version=HL7%20v2.3.1&dataType=XTN
public class XTNPhoneNumber {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "telephone_number")
    private String telephoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "area_code")
    private String areaCode;

    // [update hint] In HL7 v2.3.5 this field is renamed to "Local Number"
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "extension")
    private String extension;

    @Column(name = "any_text")
    private String anyText;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAnyText() {
        return anyText;
    }

    public void setAnyText(String anyText) {
        this.anyText = anyText;
    }
}


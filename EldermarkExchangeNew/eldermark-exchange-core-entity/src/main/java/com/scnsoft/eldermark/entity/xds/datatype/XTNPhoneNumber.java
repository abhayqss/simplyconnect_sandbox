package com.scnsoft.eldermark.entity.xds.datatype;

import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0201TelecommunicationUseCode;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0202TelecommunicationEquipmentType;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;

@Entity
@Table(name = "XTN_PhoneNumber")
public class XTNPhoneNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "telephone_number")
    private String telephoneNumber;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "telecommunication_use_code_id")
    private IDCodedValueForHL7Tables<HL7CodeTable0201TelecommunicationUseCode> telecommunicationUseCode;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "telecommunication_equipment_type_id")
    private IDCodedValueForHL7Tables<HL7CodeTable0202TelecommunicationEquipmentType> telecommunicationEquipmentType;

    @Column(name = "email", columnDefinition = "nvarchar(60)")
    @Nationalized
    private String email;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "area_code")
    private String areaCode;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "extension")
    private String extension;

    @Column(name = "any_text", columnDefinition = "nvarchar(255)")
    @Nationalized
    private String anyText;

    public XTNPhoneNumber() {
    }

    public XTNPhoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public XTNPhoneNumber(String telephoneNumber, IDCodedValueForHL7Tables<HL7CodeTable0201TelecommunicationUseCode> telecommunicationUseCode, IDCodedValueForHL7Tables<HL7CodeTable0202TelecommunicationEquipmentType> telecommunicationEquipmentType, String email, String countryCode, String areaCode, String phoneNumber, String extension, String anyText) {
        this.telephoneNumber = telephoneNumber;
        this.telecommunicationUseCode = telecommunicationUseCode;
        this.telecommunicationEquipmentType = telecommunicationEquipmentType;
        this.email = email;
        this.countryCode = countryCode;
        this.areaCode = areaCode;
        this.phoneNumber = phoneNumber;
        this.extension = extension;
        this.anyText = anyText;
    }

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

    public IDCodedValueForHL7Tables<HL7CodeTable0201TelecommunicationUseCode> getTelecommunicationUseCode() {
        return telecommunicationUseCode;
    }

    public void setTelecommunicationUseCode(IDCodedValueForHL7Tables<HL7CodeTable0201TelecommunicationUseCode> telecommunicationUseCode) {
        this.telecommunicationUseCode = telecommunicationUseCode;
    }

    public IDCodedValueForHL7Tables<HL7CodeTable0202TelecommunicationEquipmentType> getTelecommunicationEquipmentType() {
        return telecommunicationEquipmentType;
    }

    public void setTelecommunicationEquipmentType(IDCodedValueForHL7Tables<HL7CodeTable0202TelecommunicationEquipmentType> telecommunicationEquipmentType) {
        this.telecommunicationEquipmentType = telecommunicationEquipmentType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getAnyText() {
        return anyText;
    }

    public void setAnyText(String anyText) {
        this.anyText = anyText;
    }
}

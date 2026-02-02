package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(MedicalProfessionalData.TABLE_NAME)
public class MedicalProfessionalData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "Medical_Professionals";
    public static final String CODE = "Code";

    @Id
    @Column(CODE)
    private long id;

    @Column("Title_Prefix")
    private String prefixName;

    @Column("First_Name")
    private String firstName;

    @Column("Last_Name")
    private String lastName;

    @Column("Middle_Name")
    private String middleName;

    @Column("Title_Suffix")
    private String suffixName;

    @Column("Work_Phone")
    private String phone;

    @Column("Address_1")
    private String street;

    @Column("City")
    private String city;

    @Column("State")
    private String state;

    @Column("Zip_Code")
    private String zip;

    @Column("E_Mail")
    private String email;

    @Column("NPI")
    private String npi;

    @Column("Speciality")
    private String speciality;

    @Column("Inactive")
    private Boolean inactive;

    @Column("Organization")
    private String organization;

    @Column("Facility")
    private String facility;

    @Column("Ext_Pharmacy_ID")
    private String extPharmacyId;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }

    public String getPrefixName() {
        return prefixName;
    }

    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSuffixName() {
        return suffixName;
    }

    public void setSuffixName(String suffixName) {
        this.suffixName = suffixName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getExtPharmacyId() {
        return extPharmacyId;
    }

    public void setExtPharmacyId(String extPharmacyId) {
        this.extPharmacyId = extPharmacyId;
    }
}

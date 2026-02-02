package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(ResContactData.TABLE_NAME)
public class ResContactData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "res_contacts";
    public static final String UNIQUE_ID = "Unique_ID";
    public static final String RELATIONSHIP_CCDID = "Relationship_CCDID";

    @Id
    @Column(UNIQUE_ID)
    private long id;

    @Column("First_Name")
    private String firstName;

    @Column("Last_Name")
    private String lastName;

    @Column("Salutation")
    private String salutation;

    @Column("Address_Use_Alternate")
    private Boolean useAltAddress;

    @Column("Address1")
    private String streetAddress;

    @Column("City")
    private String city;

    @Column("State")
    private String state;

    @Column("Zip")
    private String zip;

    @Column("Alt_Address_Street1")
    private String altStreetAddress;

    @Column("Alt_Address_City")
    private String altCity;

    @Column("Alt_Address_State")
    private String altState;

    @Column("Alt_Address_Zip")
    private String altZip;

    @Column("Phone_1")
    private String phone1;

    @Column("Phone_2")
    private String phone2;

    @Column("Phone_3")
    private String phone3;

    @Column("Email")
    private String email;

    @Column(RELATIONSHIP_CCDID)
    private Long relationshipCcdId;

    @Column("Nearest_Relative")
    private Boolean nearestRelative;

    @Column("Responsible_Party")
    private Boolean responsibleParty;

    @Column("Emergency")
    private Boolean emergency;

    @Column("Guardian")
    private Boolean guardian;

    @Column("Designated_Person")
    private Boolean designatedPerson;

    @Column("DFPOA")
    private Boolean DFPOA;

    @Column("DHPOA")
    private Boolean DHPOA;

    @Column("Health_Care_Proxy")
    private Boolean healthCareProxy;

    @Column("MHPOA")
    private Boolean MHPOA;

    @Column("Power_of_Attorney")
    private Boolean powerOfAttorney;

    @Column("Representative")
    private Boolean representative;

    @Column("Res_Number")
    private Long resNumber;

    @Column("Facility")
    private String facility;

    @Column("priority")
    private Integer priority;

    @Column("responsible_party")
    private Boolean isResponsibleParty;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public Boolean isUseAltAddress() {
        return useAltAddress;
    }

    public void setUseAltAddress(Boolean useAltAddress) {
        this.useAltAddress = useAltAddress;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
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

    public String getAltStreetAddress() {
        return altStreetAddress;
    }

    public void setAltStreetAddress(String altStreetAddress) {
        this.altStreetAddress = altStreetAddress;
    }

    public String getAltCity() {
        return altCity;
    }

    public void setAltCity(String altCity) {
        this.altCity = altCity;
    }

    public String getAltState() {
        return altState;
    }

    public void setAltState(String altState) {
        this.altState = altState;
    }

    public String getAltZip() {
        return altZip;
    }

    public void setAltZip(String altZip) {
        this.altZip = altZip;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhone3() {
        return phone3;
    }

    public void setPhone3(String phone3) {
        this.phone3 = phone3;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getRelationshipCcdId() {
        return relationshipCcdId;
    }

    public void setRelationshipCcdId(Long relationshipCcdId) {
        this.relationshipCcdId = relationshipCcdId;
    }

    public boolean isNearestRelative() {
        return nearestRelative != null && nearestRelative;
    }

    public void setNearestRelative(Boolean nearestRelative) {
        this.nearestRelative = nearestRelative;
    }

    public boolean isResponsibleParty() {
        return responsibleParty != null && responsibleParty;
    }

    public void setResponsibleParty(Boolean responsibleParty) {
        this.responsibleParty = responsibleParty;
    }

    public boolean isEmergency() {
        return emergency != null && emergency;
    }

    public void setEmergency(Boolean emergency) {
        this.emergency = emergency;
    }

    public boolean isGuardian() {
        return guardian != null && guardian;
    }

    public void setGuardian(Boolean guardian) {
        this.guardian = guardian;
    }

    public boolean isDesignatedPerson() {
        return designatedPerson != null && designatedPerson;
    }

    public void setDesignatedPerson(Boolean designatedPerson) {
        this.designatedPerson = designatedPerson;
    }

    public boolean isDFPOA() {
        return DFPOA != null && DFPOA;
    }

    public void setDFPOA(Boolean DFPOA) {
        this.DFPOA = DFPOA;
    }

    public boolean isDHPOA() {
        return DHPOA != null && DHPOA;
    }

    public void setDHPOA(Boolean DHPOA) {
        this.DHPOA = DHPOA;
    }

    public boolean isHealthCareProxy() {
        return healthCareProxy != null && healthCareProxy;
    }

    public void setHealthCareProxy(Boolean healthCareProxy) {
        this.healthCareProxy = healthCareProxy;
    }

    public boolean isMHPOA() {
        return MHPOA != null && MHPOA;
    }

    public void setMHPOA(Boolean MHPOA) {
        this.MHPOA = MHPOA;
    }

    public boolean isPowerOfAttorney() {
        return powerOfAttorney != null && powerOfAttorney;
    }

    public void setPowerOfAttorney(Boolean powerOfAttorney) {
        this.powerOfAttorney = powerOfAttorney;
    }

    public boolean isRepresentative() {
        return representative != null && representative;
    }

    public void setRepresentative(Boolean representative) {
        this.representative = representative;
    }

    public Long getResNumber() {
        return resNumber;
    }

    public void setResNumber(Long resNumber) {
        this.resNumber = resNumber;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getResponsibleParty() {
        return isResponsibleParty;
    }
}

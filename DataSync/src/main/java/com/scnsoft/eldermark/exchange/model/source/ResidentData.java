package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

import java.sql.Date;
import java.sql.Time;

@Table(ResidentData.TABLE_NAME)
public class ResidentData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "resident";
    public static final String RES_NUMBER = "Res_Number";
    public static final String GENDER_CCDID = "Gender_CCDID";
    public static final String MARITAL_STATUS_CCDID = "Marital_Status_CCDID";
    public static final String PRIMARY_LANGUAGE_CCDID = "Primary_Language_CCDID";
    public static final String RACE_CCDID = "Race_CCDID";
    public static final String RELIGION_CCDID = "Religion_CCDID";

    @Id
    @Column(RES_NUMBER)
    private long resNumber;

    @Column("Facility")
    private String facility;

    @Column("First_Name")
    private String firstName;

    @Column("Last_Name")
    private String lastName;

    @Column("Middle_Name")
    private String middleName;

    @Column("Preferred_Name")
    private String preferredName;

    @Column("Home_Phone")
    private String homePhone;

    @Column("Other_Phone")
    private String otherPhone;

    @Column("Admit_Date")
    private Date admitDate;

    @Column("Admit_Time")
    private Time admitTime;

    @Column("BirthDate")
    private Date birthDate;

    @Column("Discharge_Date")
    private Date dischargeDate;

    @Column("Discharge_Time")
    private Time dischargeTime;

    @Column("EMail")
    private String email;

    @Column(GENDER_CCDID)
    private Long genderId;

    @Column(MARITAL_STATUS_CCDID)
    private Long maritalStatusId;

    @Column(PRIMARY_LANGUAGE_CCDID)
    private Long primaryLanguageId;

    @Column(RACE_CCDID)
    private Long raceId;

    @Column(RELIGION_CCDID)
    private Long religionId;

    @Column("Salutation")
    private String salutation;

    @Column("Social_Security")
    private String socialSecurity;

    @Column("Suffix_Name")
    private String suffixName;

    @Column("Unit_Number")
    private String unitNumber;

    @Column("Advance_Directives")
    private String advanceDirectives;

    @Column("HealthExchange_OptOut")
    private Boolean healthExchangeOptOut;

    @Column("Health_Plan")
    private String healthPlan;
    
    @Column("Resuscitate")
    private String resuscitate;
    
    @Column("Advanced_Directive_1")
    private Boolean advancedDirective1;
    
    @Column("Advanced_Directive_2")
    private Boolean advancedDirective2;
    
    @Column("Advanced_Directive_3")
    private Boolean advancedDirective3;
    
    @Column("Advanced_Directive_4")
    private Boolean advancedDirective4;
    
    @Column("Code_Status_1")
    private Boolean codeStatus1;
    
    @Column("Code_Status_2")
    private Boolean codeStatus2;
    
    @Column("Code_Status_3")
    private Boolean codeStatus3;
    
    @Column("Code_Status_4")
    private Boolean codeStatus4;

    @Column("Age")
    private Integer age;

    @Column("Dental_Insurance")
    private String dentalInsurance;

    @Column("Medical_Record_Number")
    private String medicalRecordNumber;

    @Column("Veteran")
    private String veteran;

    @Column("Prev_Addr_Street")
    private String prevAddrStreet;

    @Column("Prev_Addr_City")
    private String prevAddrCity;

    @Column("Prev_Addr_State")
    private String prevAddrState;

    @Column("Prev_Addr_Zip")
    private String prevAddrZip;

    @Column("Hospital_of_Preference")
    private String hospitalOfPreference;

    @Column("Transportation_Preference")
    private String transportationPreference;

    @Column("Ambulance_Preference")
    private String ambulancePreference;

    @Column("PreAdmission_Number")
    private String preAdmissionNumber;

    @Column("Medicare_Number")
    private String medicareNumber;

    @Column("Medicaid_Number")
    private String medicaidNumber;

    @Column("MA_Authorization_Number")
    private String MAAuthorizationNumber;

    @Column("MA_Auth_Numb_Expire_Date")
    private Date MAAuthNumbExpireDate;

    @Column("Orders")
    private String orders;

    @Column("Evacuation_Status")
    private String evacuationStatus;

    @Column("Note_Alert")
    private String noteAlert;

    @Column("Internal_Log")
    private String internalLog;

    @Column("Pharmacy_PID")
    private String pharmacyPid;

    @Override
    public Long getId() {
        return resNumber;
    }

    public void setResNumber(long resNumber) {
        this.resNumber = resNumber;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
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

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public Date getAdmitDate() {
        return admitDate;
    }

    public void setAdmitDate(Date admitDate) {
        this.admitDate = admitDate;
    }

    public Time getAdmitTime() {
        return admitTime;
    }

    public void setAdmitTime(Time admitTime) {
        this.admitTime = admitTime;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(Date dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public Time getDischargeTime() {
        return dischargeTime;
    }

    public void setDischargeTime(Time dischargeTime) {
        this.dischargeTime = dischargeTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getGenderId() {
        return genderId;
    }

    public void setGenderId(Long genderId) {
        this.genderId = genderId;
    }

    public Long getMaritalStatusId() {
        return maritalStatusId;
    }

    public void setMaritalStatusId(Long maritalStatusId) {
        this.maritalStatusId = maritalStatusId;
    }

    public String getOtherPhone() {
        return otherPhone;
    }

    public void setOtherPhone(String otherPhone) {
        this.otherPhone = otherPhone;
    }

    public Long getPrimaryLanguageId() {
        return primaryLanguageId;
    }

    public void setPrimaryLanguageId(Long primaryLanguageId) {
        this.primaryLanguageId = primaryLanguageId;
    }

    public Long getRaceId() {
        return raceId;
    }

   public void setRaceId(Long raceId) {
        this.raceId = raceId;
    }

    public Long getReligionId() {
        return religionId;
    }

    public void setReligionId(Long religionId) {
        this.religionId = religionId;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getSocialSecurity() {
        return socialSecurity;
    }

    public void setSocialSecurity(String socialSecurity) {
        this.socialSecurity = socialSecurity;
    }

    public String getSuffixName() {
        return suffixName;
    }

    public void setSuffixName(String suffixName) {
        this.suffixName = suffixName;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getAdvanceDirectives() {
        return advanceDirectives;
    }

    public void setAdvanceDirectives(String advanceDirectives) {
        this.advanceDirectives = advanceDirectives;
    }

    public Boolean getHealthExchangeOptOut() {
        return healthExchangeOptOut;
    }

    public void setHealthExchangeOptOut(Boolean healthExchangeOptOut) {
        this.healthExchangeOptOut = healthExchangeOptOut;
    }

    public String getHealthPlan() {
        return healthPlan;
    }

    public void setHealthPlan(String healthPlan) {
        this.healthPlan = healthPlan;
    }

	public String getResuscitate() {
		return resuscitate;
	}

	public void setResuscitate(String resuscitate) {
		this.resuscitate = resuscitate;
	}

	public Boolean getAdvancedDirective1() {
		return advancedDirective1;
	}

	public void setAdvancedDirective1(Boolean advancedDirective1) {
		this.advancedDirective1 = advancedDirective1;
	}

	public Boolean getAdvancedDirective2() {
		return advancedDirective2;
	}

	public void setAdvancedDirective2(Boolean advancedDirective2) {
		this.advancedDirective2 = advancedDirective2;
	}

	public Boolean getAdvancedDirective3() {
		return advancedDirective3;
	}

	public void setAdvancedDirective3(Boolean advancedDirective3) {
		this.advancedDirective3 = advancedDirective3;
	}

	public Boolean getAdvancedDirective4() {
		return advancedDirective4;
	}

	public void setAdvancedDirective4(Boolean advancedDirective4) {
		this.advancedDirective4 = advancedDirective4;
	}

	public Boolean getCodeStatus1() {
		return codeStatus1;
	}

	public void setCodeStatus1(Boolean codeStatus1) {
		this.codeStatus1 = codeStatus1;
	}

	public Boolean getCodeStatus2() {
		return codeStatus2;
	}

	public void setCodeStatus2(Boolean codeStatus2) {
		this.codeStatus2 = codeStatus2;
	}

	public Boolean getCodeStatus3() {
		return codeStatus3;
	}

	public void setCodeStatus3(Boolean codeStatus3) {
		this.codeStatus3 = codeStatus3;
	}

	public Boolean getCodeStatus4() {
		return codeStatus4;
	}

	public void setCodeStatus4(Boolean codeStatus4) {
		this.codeStatus4 = codeStatus4;
	}

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    public void setMedicalRecordNumber(String medicalRecordNumber) {
        this.medicalRecordNumber = medicalRecordNumber;
    }

    public String getVeteran() {
        return veteran;
    }

    public void setVeteran(String veteran) {
        this.veteran = veteran;
    }

    public String getPrevAddrStreet() {
        return prevAddrStreet;
    }

    public void setPrevAddrStreet(String prevAddrStreet) {
        this.prevAddrStreet = prevAddrStreet;
    }

    public String getPrevAddrCity() {
        return prevAddrCity;
    }

    public void setPrevAddrCity(String prevAddrCity) {
        this.prevAddrCity = prevAddrCity;
    }

    public String getPrevAddrState() {
        return prevAddrState;
    }

    public void setPrevAddrState(String prevAddrState) {
        this.prevAddrState = prevAddrState;
    }

    public String getPrevAddrZip() {
        return prevAddrZip;
    }

    public void setPrevAddrZip(String prevAddrZip) {
        this.prevAddrZip = prevAddrZip;
    }

    public String getHospitalOfPreference() {
        return hospitalOfPreference;
    }

    public void setHospitalOfPreference(String hospitalOfPreference) {
        this.hospitalOfPreference = hospitalOfPreference;
    }

    public String getTransportationPreference() {
        return transportationPreference;
    }

    public void setTransportationPreference(String transportationPreference) {
        this.transportationPreference = transportationPreference;
    }

    public String getAmbulancePreference() {
        return ambulancePreference;
    }

    public void setAmbulancePreference(String ambulancePreference) {
        this.ambulancePreference = ambulancePreference;
    }

    public String getPreAdmissionNumber() {
        return preAdmissionNumber;
    }

    public void setPreAdmissionNumber(String preAdmissionNumber) {
        this.preAdmissionNumber = preAdmissionNumber;
    }

    public String getMedicareNumber() {
        return medicareNumber;
    }

    public void setMedicareNumber(String medicareNumber) {
        this.medicareNumber = medicareNumber;
    }

    public String getMedicaidNumber() {
        return medicaidNumber;
    }

    public void setMedicaidNumber(String medicaidNumber) {
        this.medicaidNumber = medicaidNumber;
    }

    public String getMAAuthorizationNumber() {
        return MAAuthorizationNumber;
    }

    public void setMAAuthorizationNumber(String MAAuthorizationNumber) {
        this.MAAuthorizationNumber = MAAuthorizationNumber;
    }

    public Date getMAAuthNumbExpireDate() {
        return MAAuthNumbExpireDate;
    }

    public void setMAAuthNumbExpireDate(Date MAAuthNumbExpireDate) {
        this.MAAuthNumbExpireDate = MAAuthNumbExpireDate;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }

    public String getEvacuationStatus() {
        return evacuationStatus;
    }

    public void setEvacuationStatus(String evacuationStatus) {
        this.evacuationStatus = evacuationStatus;
    }

    public String getNoteAlert() {
        return noteAlert;
    }

    public void setNoteAlert(String noteAlert) {
        this.noteAlert = noteAlert;
    }

    public String getDentalInsurance() {
        return dentalInsurance;
    }

    public void setDentalInsurance(String dentalInsurance) {
        this.dentalInsurance = dentalInsurance;
    }

	public String getInternalLog() {
		return internalLog;
	}

	public void setInternalLog(String internalLog) {
		this.internalLog = internalLog;
	}

    public String getPharmacyPid() {
        return pharmacyPid;
    }

    public void setPharmacyPid(String pharmacyPid) {
        this.pharmacyPid = pharmacyPid;
    }
}

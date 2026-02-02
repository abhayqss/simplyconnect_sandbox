package com.scnsoft.scansol.shared;

import java.util.Date;

/**
 * Date: 19.05.15
 * Time: 5:39
 */
public class ScanSolResidentDto {
    private Long id;
    private String legacyId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String preferredName;
    private String birthDate;
    private String ssn;
    private Boolean inactive;
    private String admitDate;
    private String dischargeDate;
    private Date archiveDate;
 
    public Date getArchiveDate() {
		return archiveDate;
	}

	public void setArchiveDate(Date archiveDate) {
		this.archiveDate = archiveDate;
	}
    
	public Long getId () {
        return id;
    }

	public void setId (Long id) {
        this.id = id;
    }

    public String getFirstName () {
        return firstName;
    }

    public void setFirstName (String firstName) {
        this.firstName = firstName;
    }

    public String getLastName () {
        return lastName;
    }

    public void setLastName (String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate () {
        return birthDate;
    }

    public void setBirthDate (String birthDate) {
        this.birthDate = birthDate;
    }

    public String getSsn () {
        return ssn;
    }

    public void setSsn (String ssn) {
        this.ssn = ssn;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public String getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }
    
	public String getAdmitDate() {
		return admitDate;
	}

	public void setAdmitDate(String admitDate) {
		this.admitDate = admitDate;
	}

	public String getDischargeDate() {
		return dischargeDate;
	}

	public void setDischargeDate(String dischargeDate) {
		this.dischargeDate = dischargeDate;
	}
    
}

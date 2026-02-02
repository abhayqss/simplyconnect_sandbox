package com.scnsoft.eldermark.entity;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Master Patient Index.<br/>
 * This table doesn’t contain information about merged residents. It only contains different IDs of patient in different repositories (organizations).
 * It also contains information about patient ID in Exchange system itself. In this case, {@code residentId} and {@code patientId} are the same,
 * and {@code assigning_authority_universal} contains OID of Exchange System: 2.16.840.1.113883.3.6492.
 */
@Entity
@Table(name = "MPI")
public class MPI implements Serializable {
	
	private static final long serialVersionUID = 6202229271840160669L;


	@Id
	@Column(name="registry_patient_id")
	@GeneratedValue(generator = "system_uuid")
	@GenericGenerator(name = "system_uuid", strategy = "uuid")
	private String registryPatientId;

	@Column(name = "patient_id")
	private String patientId;

	@Column(name = "assigning_authority")
	private String assigningAuthority;

	@Column(name = "surviving_patient_id")
	private String survivingPatientId;

	@Column(name = "deleted", length = 1)
	private String deleted;

	@Column(name = "merged", length = 1)
	private String merged;

	@Column(name = "resident_id", insertable = true, updatable = true)
	private Long residentId;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	@JoinColumn(name="resident_id", insertable = false, updatable = false)
	private Resident resident;

	@Column(name = "assigning_authority_namespace")
	private String assigningAuthorityNamespace;

	@Column(name = "assigning_authority_universal")
	private String assigningAuthorityUniversal;

	@Column(name = "assigning_authority_universal_type")
	private String assigningAuthorityUniversalType;

	@Column(name = "assigning_facility_namespace")
	private String assigningFacilityNamespace;

	@Column(name = "assigning_facility_universal")
	private String assigningFacilityUniversal;

	@Column(name = "assigning_facility_universal_type")
	private String assigningFacilityUniversalType;

	@Column(name = "type_code", length = 50)
	private String identifierTypeCode;

	@Column(name = "effective_date")
	private Date effectiveDate;

	@Column(name = "expiration_date")
	private Date expirationDate;

	public void setAssigningAuthority(String assigningAuthority) {
		this.assigningAuthority = assigningAuthority;
	}
	/**
	 * @return Patient ID in an external system, that provided information about this patient
	 */
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getSurvivingPatientId() {
		return survivingPatientId;
	}
	public void setSurvivingPatientId(String survivingPatientId) {
		this.survivingPatientId = survivingPatientId;
	}
	public String getRegistryPatientId() {
		return registryPatientId;
	}
	public void setRegistryPatientId(String registryPatientId) {
		this.registryPatientId = registryPatientId;
	}

	/**
	 * @return a flag, showing if this patient record was marked as "deleted".
	 */
	public String getDeleted() {
		return deleted;
	}

	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}

	public String getMerged() {
		return merged;
	}

	public void setMerged(String merged) {
		this.merged = merged;
	}

	/**
	 * @return Local resident ID in Exchange system
	 */
	public Long getResidentId() {
		return residentId;
	}

	public void setResidentId(Long residentId) {
		this.residentId = residentId;
	}

	public String getAssigningAuthorityNamespace() {
		return assigningAuthorityNamespace;
	}

	public void setAssigningAuthorityNamespace(String assigningAuthorityNamespace) {
		this.assigningAuthorityNamespace = assigningAuthorityNamespace;
	}

	/**
	 * @return OID of an external organization, that provided patient_id
	 */
	public String getAssigningAuthorityUniversal() {
		return assigningAuthorityUniversal;
	}

	public void setAssigningAuthorityUniversal(String assigningAuthorityUniversal) {
		this.assigningAuthorityUniversal = assigningAuthorityUniversal;
	}

	public String getAssigningAuthorityUniversalType() {
		return assigningAuthorityUniversalType;
	}

	public void setAssigningAuthorityUniversalType(String assigningAuthorityUniversalType) {
		this.assigningAuthorityUniversalType = assigningAuthorityUniversalType;
	}

	public String getAssigningFacilityNamespace() {
		return assigningFacilityNamespace;
	}

	public void setAssigningFacilityNamespace(String assigningFacilityNamespace) {
		this.assigningFacilityNamespace = assigningFacilityNamespace;
	}

	public String getAssigningFacilityUniversal() {
		return assigningFacilityUniversal;
	}

	public void setAssigningFacilityUniversal(String assigningFacilityUniversal) {
		this.assigningFacilityUniversal = assigningFacilityUniversal;
	}

	public String getAssigningFacilityUniversalType() {
		return assigningFacilityUniversalType;
	}

	public void setAssigningFacilityUniversalType(String assigningFacilityUniversalType) {
		this.assigningFacilityUniversalType = assigningFacilityUniversalType;
	}

	public String getIdentifierTypeCode() {
		return identifierTypeCode;
	}

	public void setIdentifierTypeCode(String identifierTypeCode) {
		this.identifierTypeCode = identifierTypeCode;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getAssigningAuthority() {
		String assignAuthority = null;
		if(assigningAuthorityNamespace != null && assigningAuthorityUniversal != null && assigningAuthorityUniversalType !=null) {
			assignAuthority = assigningAuthorityNamespace + "&" + assigningAuthorityUniversal + "&" + assigningAuthorityUniversalType;
		} else if(assigningAuthorityNamespace == null && assigningAuthorityUniversal != null && assigningAuthorityUniversalType !=null){
			assignAuthority = "&"+ assigningAuthorityUniversal + "&"+ assigningAuthorityUniversalType;
		} else if(assigningAuthorityNamespace != null && assigningAuthorityUniversalType == null){
			assignAuthority = assigningAuthorityNamespace + "&"+ assigningAuthorityUniversal + "&";
		}
		return assignAuthority;
	}
}
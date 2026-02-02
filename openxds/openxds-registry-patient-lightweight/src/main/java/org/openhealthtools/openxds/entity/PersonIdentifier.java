/**
 *  Copyright (c) 2009-2010 Misys Open Source Solutions (MOSS) and others
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  Contributors:
 *    Misys Open Source Solutions - initial API and implementation
 *    -
 */

package org.openhealthtools.openxds.entity;

import com.misyshealthcare.connect.net.Identifier;
import org.hibernate.annotations.GenericGenerator;
import org.openhealthexchange.openpixpdq.data.PatientIdentifier;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "MPI")
public class PersonIdentifier implements Serializable {
	
	private static final long serialVersionUID = 6202229271840160669L;

	@Id
	@GeneratedValue(generator = "system_uuid")
	@GenericGenerator(name = "system_uuid", strategy = "uuid")
	@Column(name="registry_patient_id")
	private String registryPatientId;

	@Column(name="patient_id")
	private String patientId;

	@Column(name="assigning_authority")
	private String assigningAuthority;

	@Column(name="surviving_patient_id")
	private String survivingPatientId;

	@Column(name="deleted")
	private String deleted;

	@Column(name="merged")
	private String merged;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "resident_id", nullable = true)
	private Resident resident;

	@Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
	private Long residentId;

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

	@Column(name = "type_code")
	private String identifierTypeCode;

	@Column(name = "effective_date")
	private Date effectiveDate;

	@Column(name = "expiration_date")
	private Date expirationDate;

	public String getAssigningAuthority() {
		return assigningAuthority;
	}
	public void setAssigningAuthority(String assigningAuthority) {
		this.assigningAuthority = assigningAuthority;
	}
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

	public Resident getResident() {
		return resident;
	}

	public void setResident(Resident resident) {
		this.resident = resident;
	}

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

	public static PersonIdentifier createFromPatientIdentifier(org.openhealthexchange.openpixpdq.data.PersonIdentifier patientIdentifier) {
		PersonIdentifier personIdentifier = new PersonIdentifier();
		return createFromPatientIdentifier(personIdentifier, patientIdentifier);
	}

	public static PersonIdentifier createFromPatientIdentifier(PatientIdentifier patientIdentifier) {
		PersonIdentifier personIdentifier = new PersonIdentifier();
		return createFromPatientIdentifier(personIdentifier, patientIdentifier);
	}

	public static PersonIdentifier createFromPatientIdentifier(PersonIdentifier personIdentifier, org.openhealthexchange.openpixpdq.data.PersonIdentifier patientIdentifier) {
		personIdentifier.setPatientId(patientIdentifier.getId());

		if (patientIdentifier.getAssigningAuthority() != null) {
			final Identifier assigningAuthorityIdentifier = patientIdentifier.getAssigningAuthority();

			String assignFacNam = assigningAuthorityIdentifier.getNamespaceId();
			String assignFacUniversal = removeAmpersandsEncoding(assigningAuthorityIdentifier.getUniversalId());
			String assignFacUniversalType = removeAmpersandsEncoding(assigningAuthorityIdentifier.getUniversalIdType());

			personIdentifier.setAssigningAuthorityNamespace(assignFacNam);
			personIdentifier.setAssigningAuthorityUniversal(assignFacUniversal);
			personIdentifier.setAssigningAuthorityUniversalType(assignFacUniversalType);

			String assignAuthority = null;
			if(assignFacNam != null && assignFacUniversal != null && assignFacUniversalType !=null) {
				assignAuthority = assignFacNam + "&" + assignFacUniversal + "&" + assignFacUniversalType;
			} else if(assignFacNam == null && assignFacUniversal != null && assignFacUniversalType !=null){
				assignAuthority = "&"+ assignFacUniversal + "&"+ assignFacUniversalType;
			} else if(assignFacNam != null && assignFacUniversalType == null){
				assignAuthority = assignFacNam + "&"+ assignFacUniversal + "&";
			}

			personIdentifier.setAssigningAuthority(assignAuthority);
		}

		if (patientIdentifier.getEffectiveDate() != null)
			personIdentifier.setEffectiveDate(patientIdentifier.getEffectiveDate().getTime());
		if (patientIdentifier.getExpirationDate() != null)
			personIdentifier.setExpirationDate(patientIdentifier.getExpirationDate().getTime());

		personIdentifier.setIdentifierTypeCode(patientIdentifier.getIdentifierTypeCode());

		if (patientIdentifier.getAssigningFacility() != null) {
			final Identifier assigningFacilityIdentifier = patientIdentifier.getAssigningFacility();

			personIdentifier.setAssigningFacilityNamespace(assigningFacilityIdentifier.getNamespaceId());
			personIdentifier.setAssigningFacilityUniversal(removeAmpersandsEncoding(assigningFacilityIdentifier.getUniversalId()));
			personIdentifier.setAssigningFacilityUniversalType(removeAmpersandsEncoding(assigningFacilityIdentifier.getUniversalIdType()));
		}

		return personIdentifier;
	}

	public static String removeAmpersandsEncoding(String source) {
		if (source == null) {
			return null;
		}
		return source.replace("amp;", "");
	}
}
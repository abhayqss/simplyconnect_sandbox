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

package org.openhealthtools.openxds.entity.datatype;

import org.hibernate.annotations.Cascade;
import org.openhealthtools.openxds.entity.datatype.HDHierarchicDesignator;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "CX_ExtendedCompositeId")
public class CXExtendedCompositeId implements Serializable {

	private static final long serialVersionUID = 1;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name="p_id")
	private String pId;

	@ManyToOne
	@Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
	@JoinColumn(name = "assigning_authority_id")
	private HDHierarchicDesignator assigningAuthority;

//

	@ManyToOne
	@JoinColumn(name = "assigning_facility_id")
	@Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
	private HDHierarchicDesignator assigningFacility;

	@Column(name = "identifier_type_code")
	private String identifierTypeCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public HDHierarchicDesignator getAssigningAuthority() {
		return assigningAuthority;
	}

	public void setAssigningAuthority(HDHierarchicDesignator assigningAuthority) {
		this.assigningAuthority = assigningAuthority;
	}

	public HDHierarchicDesignator getAssigningFacility() {
		return assigningFacility;
	}

	public void setAssigningFacility(HDHierarchicDesignator assigningFacility) {
		this.assigningFacility = assigningFacility;
	}

	public String getIdentifierTypeCode() {
		return identifierTypeCode;
	}

	public void setIdentifierTypeCode(String identifierTypeCode) {
		this.identifierTypeCode = identifierTypeCode;
	}


	}
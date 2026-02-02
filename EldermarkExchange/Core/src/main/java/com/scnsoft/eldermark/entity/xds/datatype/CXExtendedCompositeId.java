package com.scnsoft.eldermark.entity.xds.datatype;

import org.hibernate.annotations.Cascade;

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

	@ManyToOne
	@Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
	@JoinColumn(name = "assigning_facility_id")
	private HDHierarchicDesignator assigningFacility;

	//todo switch to IS with table 0203
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
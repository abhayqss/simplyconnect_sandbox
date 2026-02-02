package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(ResMedProfessionalsData.TABLE_NAME)
public class ResMedProfessionalsData extends IdentifiableSourceEntity<Long> {
	
	public static final String TABLE_NAME = "Res_Med_Professionals";
	public static final String ID_COLUMN = "Unique_ID";
	
	@Id
	@Column(ID_COLUMN)
	private long id;
	
	@Column("Res_Number")
	private Long resNumber;
	
	@Column("Facility")
	private String facility;
	
	@Column("Med_Professional_Code")
	private Long medProfessionalCode;
	
	@Column("Role_Code")
	private Long roleCode;
	
	@Column("Rank")
	private Integer rank;

	@Override
	public Long getId() {
		return id;
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

	public Long getMedProfessionalCode() {
		return medProfessionalCode;
	}

	public void setMedProfessionalCode(Long medProfessionalCode) {
		this.medProfessionalCode = medProfessionalCode;
	}

	public Long getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(Long roleCode) {
		this.roleCode = roleCode;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}

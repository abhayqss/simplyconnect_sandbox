package com.scnsoft.eldermark.exchange.model.source;

import java.sql.Date;
import java.sql.Time;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(ResMedProviderData.TABLE_NAME)
public class ResMedProviderData extends IdentifiableSourceEntity<Long> {

	public static final String TABLE_NAME = "Res_Med_Provider";
	public static final String ID_COLUMN = "Unique_ID";
	
	@Id
	@Column(ID_COLUMN)
	private long id;
	
	@Column("Res_Number")
	private Long resNumber;
	
	@Column("Unit_Number")
	private String unitNumber;
	
	@Column("Med_Provider_ID")
	private Long medProviderId;
	
	@Column("Create_Date")
	private Date createDate;
	
	@Column("Create_Time")
	private Time createTime;
	
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

	public String getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}

	public Long getMedProviderId() {
		return medProviderId;
	}

	public void setMedProviderId(Long medProviderId) {
		this.medProviderId = medProviderId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Time getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Time createTime) {
		this.createTime = createTime;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}

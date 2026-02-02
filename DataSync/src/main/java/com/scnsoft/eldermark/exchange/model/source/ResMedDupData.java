package com.scnsoft.eldermark.exchange.model.source;

import java.sql.Date;
import java.sql.Time;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(ResMedDupData.TABLE_NAME)
public class ResMedDupData extends IdentifiableSourceEntity<Long> {
	
	public static final String TABLE_NAME = "Res_Med_Dup";
	public static final String ID_COLUMN = "Res_Med_Dup_ID";
	
	@Id
	@Column(ID_COLUMN)
	private long id;
	
	@Column("Res_Med_ID_1")
	private Long resMedId1;
	
	@Column("Res_Med_ID_2")
	private Long resMedId2;
	
	@Column("Create_Date")
	private Date createDate;
	
	@Column("Create_Time")
	private Time createTime;
	
	@Column("Waiting_For_Review")
	private Boolean waitingForReview;
	
	@Column("Not_a_Dup_Employee")
	private String notADupEmployee;
	
	@Column("Not_a_Dup_Date")
	private Date notADupDate;
	
	@Column("Not_a_Dup_Time")
	private Time notADupTime;
	
	@Column("Res_Number")
	private Long resNumber;
	
	@Column("Facility")
	private String facility;

	@Override
	public Long getId() {
		return id;
	}

	public Long getResMedId1() {
		return resMedId1;
	}

	public void setResMedId1(Long resMedId1) {
		this.resMedId1 = resMedId1;
	}

	public Long getResMedId2() {
		return resMedId2;
	}

	public void setResMedId2(Long resMedId2) {
		this.resMedId2 = resMedId2;
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

	public Boolean getWaitingForReview() {
		return waitingForReview;
	}

	public void setWaitingForReview(Boolean waitingForReview) {
		this.waitingForReview = waitingForReview;
	}

	public String getNotADupEmployee() {
		return notADupEmployee;
	}

	public void setNotADupEmployee(String notADupEmployee) {
		this.notADupEmployee = notADupEmployee;
	}

	public Date getNotADupDate() {
		return notADupDate;
	}

	public void setNotADupDate(Date notADupDate) {
		this.notADupDate = notADupDate;
	}

	public Time getNotADupTime() {
		return notADupTime;
	}

	public void setNotADupTime(Time notADupTime) {
		this.notADupTime = notADupTime;
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

	public void setId(long id) {
		this.id = id;
	}
	
}

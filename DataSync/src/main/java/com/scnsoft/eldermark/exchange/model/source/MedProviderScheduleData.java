package com.scnsoft.eldermark.exchange.model.source;

import java.sql.Date;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(MedProviderScheduleData.TABLE_NAME)
public class MedProviderScheduleData extends IdentifiableSourceEntity<Long> {
	
	public static final String TABLE_NAME = "Med_Provider_Schedule";
	public static final String ID_COLUMN = "Med_Prov_Sched_ID";
	
	@Id
	@Column(ID_COLUMN)
	private long id;
	
	@Column("Provider_ID")
	private Long providerId;
	
	@Column("Start_Date")
	private Date startDate;
	
	@Column("Checked_Out")
	private Boolean checkedOut;
	
	@Column("Checked_Out_By_Emp_ID")
	private String checkedOutByEmpId;
	
	@Column("Log")
	private String log;
	
	@Column("SM_Login_ID")
	private Long smLoginId;
	
	@Column("PrePour_Checked_Out")
	private Boolean prePourCheckedOut;
	
	@Column("PrePour_Checked_Out_Emp_ID")
	private String prePourCheckedOutEmpId;
	
	@Column("PrePour_SM_Login_ID")
	private Long prePourSmLoginId;
	
	@Column("Provider_Date")
	private String providerDate;
	
	@Column("Login_External_ID")
	private String loginExternalId;

	@Override
	public Long getId() {
		return id;
	}

	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Boolean getCheckedOut() {
		return checkedOut;
	}

	public void setCheckedOut(Boolean checkedOut) {
		this.checkedOut = checkedOut;
	}

	public String getCheckedOutByEmpId() {
		return checkedOutByEmpId;
	}

	public void setCheckedOutByEmpId(String checkedOutByEmpId) {
		this.checkedOutByEmpId = checkedOutByEmpId;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public Long getSmLoginId() {
		return smLoginId;
	}

	public void setSmLoginId(Long smLoginId) {
		this.smLoginId = smLoginId;
	}

	public Boolean getPrePourCheckedOut() {
		return prePourCheckedOut;
	}

	public void setPrePourCheckedOut(Boolean prePourCheckedOut) {
		this.prePourCheckedOut = prePourCheckedOut;
	}

	public String getPrePourCheckedOutEmpId() {
		return prePourCheckedOutEmpId;
	}

	public void setPrePourCheckedOutEmpId(String prePourCheckedOutEmpId) {
		this.prePourCheckedOutEmpId = prePourCheckedOutEmpId;
	}

	public Long getPrePourSmLoginId() {
		return prePourSmLoginId;
	}

	public void setPrePourSmLoginId(Long prePourSmLoginId) {
		this.prePourSmLoginId = prePourSmLoginId;
	}

	public String getProviderDate() {
		return providerDate;
	}

	public void setProviderDate(String providerDate) {
		this.providerDate = providerDate;
	}

	public String getLoginExternalId() {
		return loginExternalId;
	}

	public void setLoginExternalId(String loginExternalId) {
		this.loginExternalId = loginExternalId;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}

package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

import java.util.Date;

@Table(DiagnosisSetupData.TABLE_NAME)
public class DiagnosisSetupData extends IdentifiableSourceEntity<Long> {
	
	public static final String TABLE_NAME = "Diagnosis";
	public static final String ID_COLUMN = "Unique_ID";

	@Id
	@Column(ID_COLUMN)
	private long id;

	@Column("Name")
	private String name;


	@Column("Code")
	private String code;

	@Column("Inactive")
	private Boolean inactive;

	@Column("Create_Date")
	private Date createDate;

	@Column("LastMod_Stamp")
	private Long lastModStamp;

	@Column("ICD_9_CM")
	private String icd9CM;

	@Column("ICD_10_CM")
	private String icd10CM;

	@Column("ICD_10_PCS")
	private String icd10PCS;

	@Column("Manual_or_Library")
	private String manualOrLibrary;

	@Column("Standard_Code")
	private Boolean standardCode;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getInactive() {
		return inactive;
	}

	public void setInactive(Boolean inactive) {
		this.inactive = inactive;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Long getLastModStamp() {
		return lastModStamp;
	}

	public void setLastModStamp(Long lastModStamp) {
		this.lastModStamp = lastModStamp;
	}

	public String getIcd9CM() {
		return icd9CM;
	}

	public void setIcd9CM(String icd9CM) {
		this.icd9CM = icd9CM;
	}

	public String getIcd10CM() {
		return icd10CM;
	}

	public void setIcd10CM(String icd10CM) {
		this.icd10CM = icd10CM;
	}

	public String getIcd10PCS() {
		return icd10PCS;
	}

	public void setIcd10PCS(String icd10PCS) {
		this.icd10PCS = icd10PCS;
	}

	public String getManualOrLibrary() {
		return manualOrLibrary;
	}

	public void setManualOrLibrary(String manualOrLibrary) {
		this.manualOrLibrary = manualOrLibrary;
	}

	public Boolean getStandardCode() {
		return standardCode;
	}

	public void setStandardCode(Boolean standardCode) {
		this.standardCode = standardCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}

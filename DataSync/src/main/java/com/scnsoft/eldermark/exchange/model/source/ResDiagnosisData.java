package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

import java.sql.Date;
import java.sql.Time;

@Table(ResDiagnosisData.TABLE_NAME)
public class ResDiagnosisData extends IdentifiableSourceEntity<Long> {
	
	public static final String TABLE_NAME = "Res_Diagnosis";
	public static final String ID_COLUMN = "Unique_ID";
	public static final String JOINED_RESIDENT_BIRTH_DATE = "BirthDate";

	@Id
	@Column(ID_COLUMN)
	private long id;
	
	@Column("Res_Number")
	private Long resNumber;

    @Column(JOINED_RESIDENT_BIRTH_DATE)
    private Date residentBirthDate;

    @Column("Onset_Date")
	private Date onsetDate;

	@Column("Diagnosis")
	private String diagnosis;

	@Column("Note")
	private String note;

	@Column("Is_Primary")
	private Boolean isPrimary;

	@Column("Rank")
	private Integer rank;

	@Column("Resolve_Date")
	private Date resolveDate;

	@Column("Resolve_Date_Future")
	private Date resolveDateFuture;

	@Column("Create_Date")
	private Date createDate;

	@Column("Create_Time")
	private Time createTime;

	@Column("Create_User")
	private String createUser;

	@Column("Mod_Date")
	private Date modDate;

	@Column("Mod_Time")
	private Time modTime;

	@Column("Mod_User")
	private String modUser;

	@Column("Code_ICD9")
	private String codeIcd;

	@Override
	public Long getId() {
		return id;
	}

	public Long getResNumber() {
		return resNumber;
	}

	public void setResNumber(Long resnumber) {
		this.resNumber = resnumber;
	}

	public Date getOnsetDate() {
		return onsetDate;
	}

	public void setOnsetDate(Date onsetDate) {
		this.onsetDate = onsetDate;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Boolean getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(Boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public Date getResolveDate() {
		return resolveDate;
	}

	public void setResolveDate(Date resolveDate) {
		this.resolveDate = resolveDate;
	}

	public Date getResolveDateFuture() {
		return resolveDateFuture;
	}

	public void setResolveDateFuture(Date resolveDateFuture) {
		this.resolveDateFuture = resolveDateFuture;
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

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getModDate() {
		return modDate;
	}

	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}

	public Time getModTime() {
		return modTime;
	}

	public void setModTime(Time modTime) {
		this.modTime = modTime;
	}

	public String getModUser() {
		return modUser;
	}

	public void setModUser(String modUser) {
		this.modUser = modUser;
	}

	public void setId(long id) {
		this.id = id;
	}

    public Date getResidentBirthDate() {
        return residentBirthDate;
    }

    public void setResidentBirthDate(Date residentBirthDate) {
        this.residentBirthDate = residentBirthDate;
    }

	public String getCodeIcd() {
		return codeIcd;
	}

	public void setCodeIcd(String codeIcd) {
		this.codeIcd = codeIcd;
	}
}

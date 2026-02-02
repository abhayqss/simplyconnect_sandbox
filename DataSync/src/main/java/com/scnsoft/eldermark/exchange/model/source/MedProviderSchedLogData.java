package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(MedProviderSchedLogData.TABLE_NAME)
public class MedProviderSchedLogData extends IdentifiableSourceEntity<String> {

	public static final String TABLE_NAME = "Med_Provider_Sched_Log";
	public static final String ID_COLUMN = "UUID";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	@Id
	@Column(ID_COLUMN)
	private String id;
	
	@Column("Med_Prov_Sched_ID")
	private Long medProviderSchedId;
	
	@Column("Date_Time")
	private String dateTime;
	
	@Column("Employee_ID")
	private String employeeId;
	
	@Column("Description")
	private String description;
	
	@Column("More_Tag")
	private String moreTag;
	
	@Column("More_Data")
	private String moreData;
	
	@Column("Sequence")
	private Long sequence;

	@Override
	public String getId() {
		return id;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMoreTag() {
		return moreTag;
	}

	public void setMoreTag(String moreTag) {
		this.moreTag = moreTag;
	}

	public String getMoreData() {
		return moreData;
	}

	public void setMoreData(String moreData) {
		this.moreData = moreData;
	}

	public Long getSequence() {
		return sequence;
	}

	public void setSequence(Long sequence) {
		this.sequence = sequence;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getMedProviderSchedId() {
		return medProviderSchedId;
	}

	public void setMedProviderSchedId(Long medProviderSchedId) {
		this.medProviderSchedId = medProviderSchedId;
	}
	
}

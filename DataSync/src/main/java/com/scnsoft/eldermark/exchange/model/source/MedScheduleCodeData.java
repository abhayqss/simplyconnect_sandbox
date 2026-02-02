package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(MedScheduleCodeData.TABLE_NAME)
public class MedScheduleCodeData extends IdentifiableSourceEntity<Long> {
	
	public static final String TABLE_NAME = "Med_Schedule_Codes";
	public static final String ID_COLUMN = "Med_Schedule_ID";
	
	@Id
	@Column(ID_COLUMN)
	private long id;
	
	@Column("Facility")
    private String facility;
	
	@Column("Description")
	private String description;
	
	@Column("Passing_Times")
	private String passingTimes;
	
	@Column("PRN")
	private Boolean prn;
	
	@Column("Inactive")
	private Boolean inactive;
	
	@Column("SM_SIG_Code")
	private String smSigCode;
	
	@Column("Unit_Station_IDs")
	private String unitStationIds;
	
	@Column("SM_SIG_Description")
	private String smSigDescription;

	@Override
	public Long getId() {
		return id;
	}

	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPassingTimes() {
		return passingTimes;
	}

	public void setPassingTimes(String passingTimes) {
		this.passingTimes = passingTimes;
	}

	public Boolean getPrn() {
		return prn;
	}

	public void setPrn(Boolean prn) {
		this.prn = prn;
	}

	public Boolean getInactive() {
		return inactive;
	}

	public void setInactive(Boolean inactive) {
		this.inactive = inactive;
	}

	public String getSmSigCode() {
		return smSigCode;
	}

	public void setSmSigCode(String smSigCode) {
		this.smSigCode = smSigCode;
	}

	public String getUnitStationIds() {
		return unitStationIds;
	}

	public void setUnitStationIds(String unitStationIds) {
		this.unitStationIds = unitStationIds;
	}

	public String getSmSigDescription() {
		return smSigDescription;
	}

	public void setSmSigDescription(String smSigDescription) {
		this.smSigDescription = smSigDescription;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}

package com.scnsoft.eldermark.exchange.model.source;

import java.sql.Time;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(MedTimeCodeData.TABLE_NAME)
public class MedTimeCodeData extends IdentifiableSourceEntity<Long> {
	
	public static final String TABLE_NAME = "Med_Time_Codes";
	public static final String ID_COLUMN = "Unique_ID";
	
	@Id
	@Column(ID_COLUMN)
	private long id;
	
	@Column("Facility")
	private String facility;
	
	@Column("Name")
	private String name;
	
	@Column("Time_Range_Begin_Alpha")
	private String timeRangeBeginAlpha;
	
	@Column("Time_Range_End_Alpha")
	private String timeRangeEndAlpha;
	
	@Column("PRN")
	private Boolean prn;
	
	@Column("Inactive")
	private Boolean inactive;
	
	@Column("Time_Range_Begin")
	private Time timeRangeBegin;
	
	@Column("Time_Range_End")
	private Time timeRangeEnd;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTimeRangeBeginAlpha() {
		return timeRangeBeginAlpha;
	}

	public void setTimeRangeBeginAlpha(String timeRangeBeginAlpha) {
		this.timeRangeBeginAlpha = timeRangeBeginAlpha;
	}

	public String getTimeRangeEndAlpha() {
		return timeRangeEndAlpha;
	}

	public void setTimeRangeEndAlpha(String timeRangeEndAlpha) {
		this.timeRangeEndAlpha = timeRangeEndAlpha;
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

	public Time getTimeRangeBegin() {
		return timeRangeBegin;
	}

	public void setTimeRangeBegin(Time timeRangeBegin) {
		this.timeRangeBegin = timeRangeBegin;
	}

	public Time getTimeRangeEnd() {
		return timeRangeEnd;
	}

	public void setTimeRangeEnd(Time timeRangeEnd) {
		this.timeRangeEnd = timeRangeEnd;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}

package com.scnsoft.eldermark.exchange.model.source;

import java.sql.Time;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(MedProviderData.TABLE_NAME)
public class MedProviderData extends IdentifiableSourceEntity<Long> {
	
	public static final String TABLE_NAME = "Med_Provider";
	public static final String ID_COLUMN = "Med_Provider_ID";
	
	@Id
	@Column(ID_COLUMN)
	private long id;
	
	@Column("Facility")
    private String facility;
	
	@Column("Name")
	private String name;
	
	@Column("Is_a_Nurse")
	private Boolean isANurse;
	
	@Column("Shift_Start")
	private Time shiftStart;
	
	@Column("Shift_End")
	private Time shiftEnd;
	
	@Column("Units")
	private String units;
	
	@Column("Inactive")
	private Boolean inactive;

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

	public Boolean getIsANurse() {
		return isANurse;
	}

	public void setIsANurse(Boolean isANurse) {
		this.isANurse = isANurse;
	}

	public Time getShiftStart() {
		return shiftStart;
	}

	public void setShiftStart(Time shiftStart) {
		this.shiftStart = shiftStart;
	}

	public Time getShiftEnd() {
		return shiftEnd;
	}

	public void setShiftEnd(Time shiftEnd) {
		this.shiftEnd = shiftEnd;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public Boolean getInactive() {
		return inactive;
	}

	public void setInactive(Boolean inactive) {
		this.inactive = inactive;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}

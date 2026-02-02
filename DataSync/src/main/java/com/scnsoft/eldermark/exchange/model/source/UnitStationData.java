package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(UnitStationData.TABLE_NAME)
public class UnitStationData extends IdentifiableSourceEntity<Long> {

	public static final String TABLE_NAME = "Unit_Station";
    public static final String ID_COLUMN = "ID";

    @Id
    @Column(ID_COLUMN)
    private long id;
    
    @Column("Facility")
    private String facility;
    
    @Column("Code")
    private String code;
    
    @Column("Facility_Code")
    private String facilityCode;
    
    @Column("Description")
    private String description;
    
    @Column("Inactive")
    private Boolean inactive;
    
    @Column("Pharmacy_Group_Code")
    private String pharmacyGroupCode;

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getFacilityCode() {
		return facilityCode;
	}

	public void setFacilityCode(String facilityCode) {
		this.facilityCode = facilityCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getInactive() {
		return inactive;
	}

	public void setInactive(Boolean inactive) {
		this.inactive = inactive;
	}

	public String getPharmacyGroupCode() {
		return pharmacyGroupCode;
	}

	public void setPharmacyGroupCode(String pharmacyGroupCode) {
		this.pharmacyGroupCode = pharmacyGroupCode;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}

package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(UnitData.TABLE_NAME)
public class UnitData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "units";
    public static final String UNIT_ID = "Unit_ID";

    @Id
    @Column(UNIT_ID)
    private long unitId;

    @Column("Unit_Number")
    private String unitNumber;

    @Column("Unit_Type_Private_Current")
    private String unitTypePrivateCurrent;

    @Column("Facility")
    private String facility;

    @Column("Current_In_Maintenance")
    private Boolean currentInMaintenance;

    @Column("Unit_Type_SemiPrivate_A_Crnt")
    private String unitTypeSemiPrivateACrnt;

    @Column("Unit_Type_SemiPrivate_B_Crnt")
    private String unitTypeSemiPrivateBCrnt;

    @Column("Current_Division_Status")
    private String currentDivisionStatus;

    @Column("Current_Product_Type")
    private String currentProductType;

    @Column("Current_Out_Of_Service")
    private String currentOutOfService;

    @Column("Current_Model")
    private Boolean currentModel;
    
    @Column("Station_ID")
    private Long stationId;

    @Override
    public Long getId() {
        return unitId;
    }

    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getUnitTypePrivateCurrent() {
        return unitTypePrivateCurrent;
    }

    public void setUnitTypePrivateCurrent(String unitTypePrivateCurrent) {
        this.unitTypePrivateCurrent = unitTypePrivateCurrent;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public Boolean getCurrentInMaintenance() {
        return currentInMaintenance;
    }

    public void setCurrentInMaintenance(Boolean currentInMaintenance) {
        this.currentInMaintenance = currentInMaintenance;
    }

    public String getUnitTypeSemiPrivateACrnt() {
        return unitTypeSemiPrivateACrnt;
    }

    public void setUnitTypeSemiPrivateACrnt(String unitTypeSemiPrivateACrnt) {
        this.unitTypeSemiPrivateACrnt = unitTypeSemiPrivateACrnt;
    }

    public String getUnitTypeSemiPrivateBCrnt() {
        return unitTypeSemiPrivateBCrnt;
    }

    public void setUnitTypeSemiPrivateBCrnt(String unitTypeSemiPrivateBCrnt) {
        this.unitTypeSemiPrivateBCrnt = unitTypeSemiPrivateBCrnt;
    }

    public String getCurrentDivisionStatus() {
        return currentDivisionStatus;
    }

    public void setCurrentDivisionStatus(String currentDivisionStatus) {
        this.currentDivisionStatus = currentDivisionStatus;
    }

    public String getCurrentProductType() {
        return currentProductType;
    }

    public void setCurrentProductType(String currentProductType) {
        this.currentProductType = currentProductType;
    }

    public String getCurrentOutOfService() {
        return currentOutOfService;
    }

    public void setCurrentOutOfService(String currentOutOfService) {
        this.currentOutOfService = currentOutOfService;
    }

    public Boolean getCurrentModel() {
        return currentModel;
    }

    public void setCurrentModel(Boolean currentModel) {
        this.currentModel = currentModel;
    }

	public Long getStationId() {
		return stationId;
	}

	public void setStationId(Long stationId) {
		this.stationId = stationId;
	}
    
}

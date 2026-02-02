package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

import java.util.Date;

@Table(UnitHistoryData.TABLE_NAME)
public class UnitHistoryData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "unit_history";
    public static final String UNIT_HISTORY_ID = "Unit_History_ID";

    @Id
    @Column(UNIT_HISTORY_ID)
    private long unitHistoryId;

    @Column("Facility")
    private String facility;

    @Column("Unit_ID")
    private Long unitId;

    @Column("Unit_Number")
    private String unitNumber;

    @Column("SubDivide_Type")
    private String subDivideType;

    @Column("Start_Date")
    private Date startDate;

    @Column("End_Date")
    private Date endDate;

    @Column("Unit_Type")
    private String unitType;

    @Column("Product_Type")
    private String productType;


    @Column("In_Maintenance")
    private Boolean inMaintenance;

    @Column("SemiPrivate")
    private Boolean semiPrivate;

    @Override
    public Long getId() {
        return unitHistoryId;
    }

    public long getUnitHistoryId() {
        return unitHistoryId;
    }

    public void setUnitHistoryId(long unitHistoryId) {
        this.unitHistoryId = unitHistoryId;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getSubDivideType() {
        return subDivideType;
    }

    public void setSubDivideType(String subDivideType) {
        this.subDivideType = subDivideType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public Boolean getInMaintenance() {
        return inMaintenance;
    }

    public void setInMaintenance(Boolean inMaintenance) {
        this.inMaintenance = inMaintenance;
    }

    public Boolean getSemiPrivate() {
        return semiPrivate;
    }

    public void setSemiPrivate(Boolean semiPrivate) {
        this.semiPrivate = semiPrivate;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }
}

package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

import java.util.Date;

@Table(ResUnitHistoryData.TABLE_NAME)
public class ResUnitHistoryData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "res_unit_history";
    public static final String UNIQUE_ID = "Unique_ID";

    @Id
    @Column(UNIQUE_ID)
    private long id;

    @Column("Res_Number")
    private Long resNumber;

    @Column("Facility")
    private String facility;

    @Column("Unit_Number")
    private String unitNumber;

    @Column("Move_In")
    private Date moveIn;

    @Column("Move_Out")
    private Date moveOut;

    @Column("Is_Second_Occupant")
    private Boolean isSecondOccupant;

    @Column("Move_in_is_Transfer")
    private Boolean moveInIsTransfer;

    @Column("Move_out_is_Transfer")
    private Boolean moveOutIsTransfer;

    @Column("Unit_ID")
    private Long unitId;

    @Column("Res_Admit_ID")
    private Long resAdmitId;

    @Column("Notice_Given")
    private Date noticeGiven;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getResNumber() {
        return resNumber;
    }

    public void setResNumber(Long resNumber) {
        this.resNumber = resNumber;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public Date getMoveIn() {
        return moveIn;
    }

    public void setMoveIn(Date moveIn) {
        this.moveIn = moveIn;
    }

    public Date getMoveOut() {
        return moveOut;
    }

    public void setMoveOut(Date moveOut) {
        this.moveOut = moveOut;
    }

    public Boolean getSecondOccupant() {
        return isSecondOccupant;
    }

    public void setSecondOccupant(Boolean secondOccupant) {
        isSecondOccupant = secondOccupant;
    }

    public Boolean getMoveInIsTransfer() {
        return moveInIsTransfer;
    }

    public void setMoveInIsTransfer(Boolean moveInIsTransfer) {
        this.moveInIsTransfer = moveInIsTransfer;
    }

    public Boolean getMoveOutIsTransfer() {
        return moveOutIsTransfer;
    }

    public void setMoveOutIsTransfer(Boolean moveOutIsTransfer) {
        this.moveOutIsTransfer = moveOutIsTransfer;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getResAdmitId() {
        return resAdmitId;
    }

    public void setResAdmitId(Long resAdmitId) {
        this.resAdmitId = resAdmitId;
    }

    public Date getNoticeGiven() {
        return noticeGiven;
    }

    public void setNoticeGiven(Date noticeGiven) {
        this.noticeGiven = noticeGiven;
    }
}

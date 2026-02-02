package com.scnsoft.eldermark.exchange.model.source;

import java.math.BigDecimal;
import java.sql.Date;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(UnitTypesRateHistData.TABLE_NAME)
public class UnitTypesRateHistData extends IdentifiableSourceEntity<Long> {
	public static final String TABLE_NAME = "Unit_Types_Rate_Hist";
    public static final String ID_COLUMN = "Unique_ID";
    
    @Id
	@Column(ID_COLUMN)
	private long id;
    
    @Column("Unit_Types_Code")
    private String unitTypesCode;
    
    @Column("Start_Date")
    private Date startDate;
    
    @Column("End_Date")
    private Date endDate;
    
    @Column("Monthly_Rate")
    private BigDecimal monthlyRate;
    
    @Column("Daily_Rate")
    private BigDecimal dailyRate;
    
    @Column("End_Date_Future")
    private Date endDateFuture;
    
	@Override
	public Long getId() {
		return id;
	}
	
	public String getUnitTypesCode() {
		return unitTypesCode;
	}

	public void setUnitTypesCode(String unitTypesCode) {
		this.unitTypesCode = unitTypesCode;
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

	public BigDecimal getMonthlyRate() {
		return monthlyRate;
	}

	public void setMonthlyRate(BigDecimal monthlyRate) {
		this.monthlyRate = monthlyRate;
	}

	public BigDecimal getDailyRate() {
		return dailyRate;
	}

	public void setDailyRate(BigDecimal dailyRate) {
		this.dailyRate = dailyRate;
	}

	public Date getEndDateFuture() {
		return endDateFuture;
	}

	public void setEndDateFuture(Date endDateFuture) {
		this.endDateFuture = endDateFuture;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}

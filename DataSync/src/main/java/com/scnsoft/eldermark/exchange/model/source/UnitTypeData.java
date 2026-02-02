package com.scnsoft.eldermark.exchange.model.source;

import java.math.BigDecimal;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(UnitTypeData.TABLE_NAME)
public class UnitTypeData extends IdentifiableSourceEntity<String> {
    public static final String TABLE_NAME = "unit_types";
    public static final String CODE = "Code";

    @Id
    @Column(CODE)
    private String code;

    @Column("Description")
    private String description;

    @Column("Facility")
    private String facility;

    @Column("Outpatient")
    private Boolean outpatient;

    @Column("Inactive")
    private Boolean inactive;

    @Column("SemiPrivate")
    private Boolean semiPrivate;
    
    @Column("Monthly_Rate")
    private BigDecimal monthlyRate;
    
    @Column("Daily_Rate")
    private BigDecimal dailyRate;

    @Override
    public String getId() {
        return code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public Boolean getOutpatient() {
        return outpatient;
    }

    public void setOutpatient(Boolean outpatient) {
        this.outpatient = outpatient;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    public Boolean getSemiPrivate() {
        return semiPrivate;
    }

    public void setSemiPrivate(Boolean semiPrivate) {
        this.semiPrivate = semiPrivate;
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
    
}

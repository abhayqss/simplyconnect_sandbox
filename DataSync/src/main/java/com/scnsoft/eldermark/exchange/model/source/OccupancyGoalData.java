package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(OccupancyGoalData.TABLE_NAME)
public class OccupancyGoalData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "occupancy_goal";
    public static final String UNIQUE_ID = "Unique_ID";

    @Id
    @Column(UNIQUE_ID)
    private long id;

    @Column("Facility")
    private String facility;

    @Column("Month")
    private Long month;

    @Column("HeadCount_Goal")
    private Long headCountGoal;

    @Column("Units_Occupied_Goal")
    private Long unitsOccupiedGoal;

    @Column("Budgeted_Census")
    private Long budgetedCensus;

    @Column("Is_Startup")
    private Boolean isStartup;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public Long getMonth() {
        return month;
    }

    public void setMonth(Long month) {
        this.month = month;
    }

    public Long getHeadCountGoal() {
        return headCountGoal;
    }

    public void setHeadCountGoal(Long headCountGoal) {
        this.headCountGoal = headCountGoal;
    }

    public Long getUnitsOccupiedGoal() {
        return unitsOccupiedGoal;
    }

    public void setUnitsOccupiedGoal(Long unitsOccupiedGoal) {
        this.unitsOccupiedGoal = unitsOccupiedGoal;
    }

    public Long getBudgetedCensus() {
        return budgetedCensus;
    }

    public void setBudgetedCensus(Long budgetedCensus) {
        this.budgetedCensus = budgetedCensus;
    }

    public Boolean getStartup() {
        return isStartup;
    }

    public void setStartup(Boolean startup) {
        isStartup = startup;
    }
}

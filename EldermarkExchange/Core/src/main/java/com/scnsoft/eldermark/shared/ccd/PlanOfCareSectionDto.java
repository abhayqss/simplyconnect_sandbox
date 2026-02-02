package com.scnsoft.eldermark.shared.ccd;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.scnsoft.eldermark.shared.json.CustomDateSerializer;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by stsiushkevich on 5/5/2015.
 */
@Entity
@Table(name = "MedicationSectionDto")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "exec__load_ccd_plan_of_care",
                query = "EXEC dbo.load_ccd_plan_of_care " +
                        "@ResidentId = :residentId," +
                        "@SortBy = :sortBy," +
                        "@SortDir = :sortDir," +
                        "@Offset = :offset," +
                        "@Limit = :limit," +
                        "@Aggregated = :aggregated",
                resultClass = PlanOfCareSectionDto.class
        ),
        @NamedNativeQuery(
                name = "exec__load_ccd_plan_of_care_count",
                query = "EXEC dbo.load_ccd_plan_of_care_count " +
                        "@ResidentId = :residentId," +
                        "@Aggregated = :aggregated",
                resultClass = CountDto.class
        )
})
public class PlanOfCareSectionDto extends CcdSectionDto {
    @Column(name = "pl_of_care_activity")
    private String plannedActivity;

    @Column(name = "pl_of_care_date")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date plannedDate;

    @Column(name = "is_free_text")
    private Boolean isFreeText;

    @Column(name = "pl_of_care_id")
    private Long planOfCareId;

    public String getPlannedActivity() {
        return plannedActivity;
    }

    public void setPlannedActivity(String plannedActivity) {
        this.plannedActivity = plannedActivity;
    }

    public Date getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(Date plannedDate) {
        this.plannedDate = plannedDate;
    }

    public Boolean getIsFreeText() {
        return isFreeText;
    }

    public void setIsFreeText(Boolean isFreeText) {
        isFreeText = isFreeText;
    }

    public Long getPlanOfCareId() {
        return planOfCareId;
    }

    public void setPlanOfCareId(Long planOfCareId) {
        this.planOfCareId = planOfCareId;
    }
}

package com.scnsoft.eldermark.shared.ccd;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.scnsoft.eldermark.shared.json.CustomDateSerializer;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ProblemSectionDto")
@NamedNativeQueries({
        @NamedNativeQuery (
                name = "exec__load_ccd_problems",
                query = "EXEC dbo.load_ccd_problems " +
                        "@ResidentId = :residentId," +
                        "@SortBy = :sortBy," +
                        "@SortDir = :sortDir," +
                        "@Offset = :offset," +
                        "@Limit = :limit," +
                        "@Aggregated = :aggregated",
                resultClass = ProblemSectionDto.class
        ),
        @NamedNativeQuery (
                name = "exec__load_ccd_problems_count",
                query = "EXEC dbo.load_ccd_problems_count " +
                        "@ResidentId = :residentId," +
                        "@Aggregated = :aggregated",
                resultClass = CountDto.class
        )
})
public class ProblemSectionDto extends CcdSectionDto {
    @Column(name="problem_name")
    private String name;

    @Column(name="problem_status_text")
    private String status;

    @Column(name = "effective_time_low")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date startDate;

    @Column(name = "effective_time_high")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date endDate;

    @Column(name = "problem_value_code")
    private String problemValueCode;

    @Column(name = "problem_value_code_set")
    private String problemValueCodeSet;

    @Column(name = "problem_type_text")
    private String type;

    @Column(name = "problem_observation_id")
    private Long problemObservationId;

    //todo delete when all sections are implemented
    @Column(name = "is_manual")
    private Boolean isManual;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getProblemValueCode() {
        return problemValueCode;
    }

    public void setProblemValueCode(String problemValueCode) {
        this.problemValueCode = problemValueCode;
    }

    public String getProblemValueCodeSet() {
        return problemValueCodeSet;
    }

    public void setProblemValueCodeSet(String problemValueCodeSet) {
        this.problemValueCodeSet = problemValueCodeSet;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getProblemObservationId() {
        return problemObservationId;
    }

    public void setProblemObservationId(Long problemObservationId) {
        this.problemObservationId = problemObservationId;
    }

    @Override
    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }
}

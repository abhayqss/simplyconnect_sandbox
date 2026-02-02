package com.scnsoft.eldermark.shared.ccd;

import com.scnsoft.eldermark.shared.json.CustomDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by stsiushkevich on 4/30/2015.
 */
@Entity
@Table(name = "ImmunizationSectionDto")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "exec__load_ccd_immunizations",
                query = "EXEC dbo.load_ccd_immunizations " +
                        "@ResidentId = :residentId," +
                        "@SortBy = :sortBy," +
                        "@SortDir = :sortDir," +
                        "@Offset = :offset," +
                        "@Limit = :limit," +
                        "@Aggregated = :aggregated",
                resultClass = ImmunizationSectionDto.class
        ),
        @NamedNativeQuery (
                name = "exec__load_ccd_immunizations_count",
                query = "EXEC dbo.load_ccd_immunizations_count " +
                        "@ResidentId = :residentId," +
                        "@Aggregated = :aggregated",
                resultClass = CountDto.class
        )
})
public class ImmunizationSectionDto extends CcdSectionDto{
    @Column(name = "imm_text")
    private String vaccine;

    @Column(name = "imm_started")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date startDate;

    @Column(name = "imm_stopped")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date endDate;

    @Column(name = "imm_status")
    private String status;

    public String getVaccine() {
        return vaccine;
    }

    public void setVaccine(String vaccine) {
        this.vaccine = vaccine;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

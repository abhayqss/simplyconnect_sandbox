package com.scnsoft.eldermark.shared.ccd;

import com.scnsoft.eldermark.shared.json.CustomDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ProcedureSectionDto")
@NamedNativeQueries({
        @NamedNativeQuery (
                name = "exec__load_ccd_procedures",
                query = "EXEC dbo.load_ccd_procedures " +
                        "@ResidentId = :residentId," +
                        "@SortBy = :sortBy," +
                        "@SortDir = :sortDir," +
                        "@Offset = :offset," +
                        "@Limit = :limit," +
                        "@Aggregated = :aggregated",
                resultClass = ProcedureSectionDto.class
        ),
        @NamedNativeQuery (
                name = "exec__load_ccd_procedures_count",
                query = "EXEC dbo.load_ccd_procedures_count " +
                        "@ResidentId = :residentId," +
                        "@Aggregated = :aggregated",
                resultClass = CountDto.class
        )
})
public class ProcedureSectionDto extends CcdSectionDto {
    @Column(name = "procedure_type_text")
    private String type;

    @Column(name = "procedure_started")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date startDate;

    @Column(name = "procedure_stopped")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date endDate;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}

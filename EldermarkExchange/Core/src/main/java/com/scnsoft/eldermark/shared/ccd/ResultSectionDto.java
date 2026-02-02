package com.scnsoft.eldermark.shared.ccd;

import com.scnsoft.eldermark.shared.json.CustomDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by stsiushkevich on 25-04-2015.
 */
@Entity
@Table(name = "ResultSectionDto")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "exec__load_ccd_results",
                query = "EXEC dbo.load_ccd_results " +
                        "@ResidentId = :residentId," +
                        "@SortBy = :sortBy," +
                        "@SortDir = :sortDir," +
                        "@Offset = :offset," +
                        "@Limit = :limit," +
                        "@Aggregated = :aggregated",
                resultClass = ResultSectionDto.class
        ),
        @NamedNativeQuery (
                name = "exec__load_ccd_results_count",
                query = "EXEC dbo.load_ccd_results_count " +
                        "@ResidentId = :residentId," +
                        "@Aggregated = :aggregated",
                resultClass = CountDto.class
        )
})
public class ResultSectionDto extends CcdSectionDto {

    @Column(name = "result_date")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date date;

    @Column(name = "result_text")
    private String type;

    @Column(name = "result_status_code")
    private String statusCode;

    @Column(name = "result_val_unit")
    private String value;

    @Column(name = "result_interpretation_codes")
    private String interpretations;


    @Column(name = "result_ref_ranges")
    private String referenceRanges;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getInterpretations() {
        return interpretations;
    }

    public void setInterpretations(String interpretations) {
        this.interpretations = interpretations;
    }

    public String getReferenceRanges() {
        return referenceRanges;
    }

    public void setReferenceRanges(String referenceRanges) {
        this.referenceRanges = referenceRanges;
    }
}

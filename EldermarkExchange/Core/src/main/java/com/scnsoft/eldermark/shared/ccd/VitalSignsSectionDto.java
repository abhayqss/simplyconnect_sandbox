package com.scnsoft.eldermark.shared.ccd;

import com.scnsoft.eldermark.shared.json.CustomDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by stsiushkevich on 4/30/2015.
 */
@Entity
@Table(name = "VitalSignsSectionDto")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "exec__load_ccd_vital_signs",
                query = "EXEC dbo.load_ccd_vital_signs " +
                        "@ResidentId = :residentId," +
                        "@SortBy = :sortBy," +
                        "@SortDir = :sortDir," +
                        "@Offset = :offset," +
                        "@Limit = :limit," +
                        "@Aggregated = :aggregated",
                resultClass = VitalSignsSectionDto.class
        ),
        @NamedNativeQuery (
                name = "exec__load_ccd_vital_signs_count",
                query = "EXEC dbo.load_ccd_vital_signs_count " +
                        "@ResidentId = :residentId," +
                        "@Aggregated = :aggregated",
                resultClass = CountDto.class
        )
})
public class VitalSignsSectionDto extends CcdSectionDto{
    @Column(name = "vs_date")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date date;

    @Column(name = "vs_value")
    private String value;

    @Column(name = "vs_res_type")
    private String type;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

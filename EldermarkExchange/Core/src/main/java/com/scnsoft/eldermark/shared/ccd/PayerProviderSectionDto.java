package com.scnsoft.eldermark.shared.ccd;

import com.scnsoft.eldermark.shared.json.CustomDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by stsiushkevich on 5/4/2015.
 */
@Entity
@Table(name = "PayerDriverDto")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "exec__load_ccd_payer_providers",
                query = "EXEC dbo.load_ccd_payer_providers " +
                        "@ResidentId = :residentId," +
                        "@SortBy = :sortBy," +
                        "@SortDir = :sortDir," +
                        "@Offset = :offset," +
                        "@Limit = :limit," +
                        "@Aggregated = :aggregated",
                resultClass = PayerProviderSectionDto.class
        ),
        @NamedNativeQuery (
                name = "exec__load_ccd_payer_providers_count",
                query = "EXEC dbo.load_ccd_payer_providers_count " +
                        "@ResidentId = :residentId," +
                        "@Aggregated = :aggregated",
                resultClass = CountDto.class
        )
})
public class PayerProviderSectionDto extends CcdSectionDto {
    @Column(name = "payer_providers_insurance_info")
    private String insuranceInfo;

    @Column(name = "payer_providers_insurance_member_id")
    private String insuranceType;

    @Column(name = "payer_providers_time_heigh")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date coverageDateStart;

    @Column(name = "payer_providers_time_low")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date coverageDateEnd;

    public String getInsuranceInfo() {
        return insuranceInfo;
    }

    public void setInsuranceInfo(String insuranceInfo) {
        this.insuranceInfo = insuranceInfo;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public Date getCoverageDateStart() {
        return coverageDateStart;
    }

    public void setCoverageDateStart(Date coverageDateStart) {
        this.coverageDateStart = coverageDateStart;
    }

    public Date getCoverageDateEnd() {
        return coverageDateEnd;
    }

    public void setCoverageDateEnd(Date coverageDateEnd) {
        this.coverageDateEnd = coverageDateEnd;
    }
}

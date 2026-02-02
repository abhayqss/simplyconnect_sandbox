package com.scnsoft.eldermark.shared.ccd;

import com.scnsoft.eldermark.shared.json.CustomDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by stsiushkevich on 4/29/2015.
 */
@Entity
@Table(name = "EncounterSectionDto")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "exec__load_ccd_encounters",
                query = "EXEC dbo.load_ccd_encounters " +
                        "@ResidentId = :residentId," +
                        "@SortBy = :sortBy," +
                        "@SortDir = :sortDir," +
                        "@Offset = :offset," +
                        "@Limit = :limit," +
                        "@Aggregated = :aggregated",
                resultClass = EncounterSectionDto.class
        ),
        @NamedNativeQuery (
                name = "exec__load_ccd_encounters_count",
                query = "EXEC dbo.load_ccd_encounters_count " +
                        "@ResidentId = :residentId," +
                        "@Aggregated = :aggregated",
                resultClass = CountDto.class
        )
})
public class EncounterSectionDto extends CcdSectionDto {
    @Column(name = "encounter_date")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date date;

    @Column(name = "encounter_type_text")
    private String type;

    @Column(name = "encounter_provider_codes")
    private String providerCodes;

    @Column(name = "encounter_service_delivery_locations")
    private String serviceDeliveryLocations;

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

    public String getProviderCodes() {
        return providerCodes;
    }

    public void setProviderCodes(String providerCodes) {
        this.providerCodes = providerCodes;
    }

    public String getServiceDeliveryLocations() {
        return serviceDeliveryLocations;
    }

    public void setServiceDeliveryLocations(String serviceDeliveryLocations) {
        this.serviceDeliveryLocations = serviceDeliveryLocations;
    }
}

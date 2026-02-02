package com.scnsoft.eldermark.shared.ccd;

import com.scnsoft.eldermark.shared.json.CustomDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by stsiushkevich on 5/5/2015.
 */
@Entity
@Table(name = "MedicalEquipmentSectionDto")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "exec__load_ccd_medical_equipment",
                query = "EXEC dbo.load_ccd_medical_equipment " +
                        "@ResidentId = :residentId," +
                        "@SortBy = :sortBy," +
                        "@SortDir = :sortDir," +
                        "@Offset = :offset," +
                        "@Limit = :limit," +
                        "@Aggregated = :aggregated",
                resultClass = MedicalEquipmentSectionDto.class
        ),
        @NamedNativeQuery (
                name = "exec__load_ccd_medical_equipment_count",
                query = "EXEC dbo.load_ccd_medical_equipment_count " +
                        "@ResidentId = :residentId," +
                        "@Aggregated = :aggregated",
                resultClass = CountDto.class
        )
})
public class MedicalEquipmentSectionDto extends CcdSectionDto{
    @Column(name = "medical_equipment_device")
    private String device;

    @Column(name = "medical_equipment_time_high")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date suppliedDate;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Date getSuppliedDate() {
        return suppliedDate;
    }

    public void setSuppliedDate(Date suppliedDate) {
        this.suppliedDate = suppliedDate;
    }
}

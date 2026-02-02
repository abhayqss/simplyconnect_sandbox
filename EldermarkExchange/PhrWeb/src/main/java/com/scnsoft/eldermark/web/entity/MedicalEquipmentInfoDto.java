package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent medical equipment in list
 */
@ApiModel(description = "This DTO is intended to represent medical equipment in list")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-06T13:30:20.514+03:00")
public class MedicalEquipmentInfoDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("supplyDevice")
    private String supplyDevice = null;

    @JsonProperty("dateSupplied")
    private Long dateSupplied = null;




    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * supply/device
     *
     * @return supplyDevice
     */

    @ApiModelProperty(example = "Wheelchair", value = "supply/device")
    public String getSupplyDevice() {
        return supplyDevice;
    }

    public void setSupplyDevice(String supplyDevice) {
        this.supplyDevice = supplyDevice;
    }

    /**
     * date supplied
     *
     * @return dateSupplied
     */

    @ApiModelProperty(example = "1326862800000", value = "date supplied")
    public Long getDateSupplied() {
        return dateSupplied;
    }

    public void setDateSupplied(Long dateSupplied) {
        this.dateSupplied = dateSupplied;
    }

}

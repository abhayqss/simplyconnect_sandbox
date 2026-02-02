package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent medical equipment details
 */
@ApiModel(description = "This DTO is intended to represent medical equipment details")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-06T13:30:20.514+03:00")
public class MedicalEquipmentDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("supplyDevice")
    private String supplyDevice = null;

    @JsonProperty("dateSupplied")
    private Long dateSupplied = null;

    @JsonProperty("status")
    private String status = null;

    @JsonProperty("quantity")
    private Integer quantity = null;

    @JsonProperty("dataSource")
    private DataSourceDto dataSource = null;




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

    /**
     * medical equipment status
     *
     * @return status
     */

    @ApiModelProperty(example = "Active", value = "medical equipment status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * quantity
     *
     * @return quantity
     */

    @ApiModelProperty(example = "1", value = "quantity")
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }



    @ApiModelProperty(value = "")
    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }

}

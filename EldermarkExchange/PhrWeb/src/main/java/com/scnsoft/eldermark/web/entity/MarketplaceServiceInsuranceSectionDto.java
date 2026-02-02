package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent marketplace insurance service section
 */
@ApiModel(description = "This DTO is intended to represent marketplace insurance service section")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-14T12:47:41.150+03:00")
public class MarketplaceServiceInsuranceSectionDto {

    @JsonProperty("key")
    private String key = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("order")
    private Long order = null;

    @JsonProperty("data")
    private List<MarketplaceServiceInsuranceSectionValueDto> data = new ArrayList<MarketplaceServiceInsuranceSectionValueDto>();


    @ApiModelProperty(value = "")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @ApiModelProperty(value = "")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(value = "")
    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }
    public MarketplaceServiceInsuranceSectionDto addDataItem(MarketplaceServiceInsuranceSectionValueDto dataItem) {
        this.data.add(dataItem);
        return this;
    }

    /**
     * It contains a list of values.
     * @return data
     */
    @ApiModelProperty(value = "It contains a list of values.")
    public List<MarketplaceServiceInsuranceSectionValueDto> getData() {
        return data;
    }

    public void setData(List<MarketplaceServiceInsuranceSectionValueDto> data) {
        this.data = data;
    }

}



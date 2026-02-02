package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;

/**
 * This DTO is intended to represent plan of care activity
 */
@ApiModel(description = "This DTO is intended to represent plan of care activity")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-06T17:04:53.931+03:00")
public class PlanOfCareDto extends PlanOfCareInfoDto {

    @JsonProperty("dataSource")
    private DataSourceDto dataSource = null;

    @ApiModelProperty(value = "")
    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }

}


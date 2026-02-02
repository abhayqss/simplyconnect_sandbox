package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to sub total and total count
 */
@ApiModel(description = "This DTO is intended to sub total and total count")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-17T15:23:55.039+03:00")
public class CountDto {

    @JsonProperty("count")
    private Long count = null;

    @JsonProperty("totalCount")
    private Long totalCount = null;


    /**
     * Sub total count (using search criteria)
     * @return count
     */
    @ApiModelProperty(value = "Sub total count (using search criteria)")
    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    /**
     * Total count
     * @return totalCount
     */
    @ApiModelProperty(value = "Total count")
    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

}
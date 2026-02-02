package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import java.util.Date;

/**
 * This Dto is intended to represent list entry of procedure
 */
@ApiModel(description = "This Dto is intended to represent list entry of procedure")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-06T13:18:22.383+03:00")
public class ProcedureListItemDto extends ListItemDto{

    @JsonProperty("identifiedDate")
    private Date identifiedDate = null;

    /**
     * Date Identified
     *
     * @return identifiedDate
     */

    @ApiModelProperty(example = "1530870210098", value = "Date Identified")
    public Date getIdentifiedDate() {
        return identifiedDate;
    }

    public void setIdentifiedDate(Date identifiedDate) {
        this.identifiedDate = identifiedDate;
    }

}

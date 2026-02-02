package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.entity.phr.InvitationActivity;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-05-02T16:19:34.209+03:00")
public class InvitationActivityDto extends ActivityDto {

  @JsonProperty("status")
  private InvitationActivity.Status status = null;


  /**
   * Invitation status
   * @return status
   */
  @ApiModelProperty(value = "Invitation status")
  public InvitationActivity.Status getStatus() {
    return status;
  }

  public void setStatus(InvitationActivity.Status status) {
    this.status = status;
  }

}


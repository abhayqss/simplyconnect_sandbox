package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent pregnancy observation details
 */
@ApiModel(description = "This DTO is intended to represent pregnancy observation details")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-05T18:28:37.285+03:00")
public class SocialHistoryCountDto {

    @JsonProperty("PREGNANCY_OBSERVATION")
    private Long pregnancyCount = null;

    @JsonProperty("SMOKING_STATUS_OBSERVATION")
    private Long smokingStatusCount = null;

    @JsonProperty("SOCIAL_HISTORY_OBSERVATION")
    private Long socialHistoryCount = null;

    @JsonProperty("TOBACCO_USE")
    private Long tobaccoUseCount = null;


    @ApiModelProperty(value = "")
    public Long getPregnancyCount() {
        return pregnancyCount;
    }

    public void setPregnancyCount(Long pregnancyCount) {
        this.pregnancyCount = pregnancyCount;
    }



    @ApiModelProperty(value = "")
    public Long getSmokingStatusCount() {
        return smokingStatusCount;
    }

    public void setSmokingStatusCount(Long smokingStatusCount) {
        this.smokingStatusCount = smokingStatusCount;
    }



    @ApiModelProperty(value = "")
    public Long getSocialHistoryCount() {
        return socialHistoryCount;
    }

    public void setSocialHistoryCount(Long socialHistoryCount) {
        this.socialHistoryCount = socialHistoryCount;
    }



    @ApiModelProperty(value = "")
    public Long getTobaccoUseCount() {
        return tobaccoUseCount;
    }

    public void setTobaccoUseCount(Long tobaccoUseCount) {
        this.tobaccoUseCount = tobaccoUseCount;
    }

}

package com.scnsoft.eldermark.api.external.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.api.shared.validation.Uuid;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T14:21:48.776+03:00")
public class NucleusInfoDto {

    @NotNull
    @Uuid
    @JsonProperty("userId")
    private String userId = null;

    @JsonProperty("familyCareTeamMemberId")
    private Long familyCareTeamMemberId = null;


    /**
     * nucleus user id
     *
     * @return userId
     */
    @ApiModelProperty(example = "dfa1953f-e401-442f-bbcf-bde33b3ca018", value = "nucleus user id (36-char UUID)")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * nucleus family CTM id
     *
     * @return familyCareTeamMemberId
     */
    @ApiModelProperty(value = "nucleus family CTM id")
    public Long getFamilyCareTeamMemberId() {
        return familyCareTeamMemberId;
    }

    public void setFamilyCareTeamMemberId(Long familyCareTeamMemberId) {
        this.familyCareTeamMemberId = familyCareTeamMemberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NucleusInfoDto that = (NucleusInfoDto) o;

        return new EqualsBuilder()
                .append(getUserId(), that.getUserId())
                .append(getFamilyCareTeamMemberId(), that.getFamilyCareTeamMemberId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getUserId())
                .append(getFamilyCareTeamMemberId())
                .toHashCode();
    }

}

package com.scnsoft.eldermark.web.commons.dto.careteam;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CareTeamInvitationResendDto extends BaseCareTeamInvitationDto {

    @JsonIgnore
    private Long id;

    @NotEmpty
    @Size(max = 256)
    @Pattern(regexp = ValidationRegExpConstants.EMAIL_REGEXP)
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

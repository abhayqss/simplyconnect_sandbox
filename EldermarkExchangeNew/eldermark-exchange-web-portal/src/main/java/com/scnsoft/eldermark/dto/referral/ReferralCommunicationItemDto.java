package com.scnsoft.eldermark.dto.referral;

import com.scnsoft.eldermark.validation.ValidationRegExpConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ReferralCommunicationItemDto {

    private Long date;

    @NotEmpty
    @Size(max = 256)
    private String authorFullName;

    @NotEmpty
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String authorPhone;

    @NotEmpty
    @Size(max = 20000)
    private String text;

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getAuthorFullName() {
        return authorFullName;
    }

    public void setAuthorFullName(String authorFullName) {
        this.authorFullName = authorFullName;
    }

    public String getAuthorPhone() {
        return authorPhone;
    }

    public void setAuthorPhone(String authorPhone) {
        this.authorPhone = authorPhone;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

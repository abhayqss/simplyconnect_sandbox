package com.scnsoft.eldermark.mobile.dto.auth.password;

import javax.validation.constraints.NotEmpty;

public class PasswordChangeDto extends PasswordResetRequestDto {

    @NotEmpty
    private String oldPassword;
    @NotEmpty
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

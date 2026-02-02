package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.mobile.dto.LoginDto;
import com.scnsoft.eldermark.mobile.dto.RoomSidUserDto;
import com.scnsoft.eldermark.mobile.dto.RoomTokenLoginDto;
import com.scnsoft.eldermark.mobile.dto.UserDto;
import com.scnsoft.eldermark.mobile.dto.auth.password.PasswordChangeDto;
import com.scnsoft.eldermark.mobile.dto.auth.password.PasswordComplexityRuleDto;
import com.scnsoft.eldermark.mobile.dto.auth.password.PasswordResetDto;
import com.scnsoft.eldermark.mobile.dto.auth.password.PasswordResetRequestDto;

import java.util.List;

public interface AuthFacade {
    UserDto login(LoginDto loginDto);

    void requestPasswordReset(PasswordResetRequestDto passwordResetRequestDto);

    void resetPassword(PasswordResetDto dto);

    void changePassword(PasswordChangeDto dto);

    List<PasswordComplexityRuleDto> getPasswordComplexityRules(Long organizationId, String companyCode);

    RoomSidUserDto loginByRoomToken(RoomTokenLoginDto roomTokenLoginDto);
}

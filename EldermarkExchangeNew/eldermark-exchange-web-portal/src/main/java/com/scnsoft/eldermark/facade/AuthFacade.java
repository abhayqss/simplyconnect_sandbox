package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.PasswordComplexityRules;
import com.scnsoft.eldermark.dto.UserDto;
import com.scnsoft.eldermark.dto.password.PasswordChangeDto;
import com.scnsoft.eldermark.dto.password.PasswordResetDto;
import com.scnsoft.eldermark.dto.password.CreatePasswordExternalDto;
import com.scnsoft.eldermark.dto.password.PasswordResetRequestDto;
import com.scnsoft.eldermark.entity.Employee;

public interface AuthFacade {

    void requestPasswordReset(PasswordResetRequestDto dto);

    void createPasswordAndActivateAccount(PasswordResetDto dto);

    void createPasswordAndActivateAccountExternal(CreatePasswordExternalDto dto);

    void resetPassword(PasswordResetDto dto);

    void changePassword(PasswordChangeDto dto);

    PasswordComplexityRules getPasswordComplexityRules(Long organizationId, String companyId);

    UserDto getUser(Long id);

    UserDto getUserWithConversationToken(Long id);

    void declineInvitation(String token);

    void validateInvitationToken(String token);

    void validateInvitationTokenExternal(String token);

    void validateResetPasswordRequestToken(String token);

    void processBadCredentialsError(String username, String companyId);

    long lockedMinutesLeft(String username, String companyId);

    void processSuccessfulLogin(Long employeeId);

    boolean validateLogin(String login, Long organizationId);

    Employee findActiveByLegacyIdAndLoginCompanyId(String legacyId, String loginCompanyId);
}

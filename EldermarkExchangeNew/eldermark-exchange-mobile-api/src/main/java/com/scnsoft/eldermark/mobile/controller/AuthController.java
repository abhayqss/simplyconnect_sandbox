package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.mobile.dto.LoginDto;
import com.scnsoft.eldermark.mobile.dto.RoomSidUserDto;
import com.scnsoft.eldermark.mobile.dto.RoomTokenLoginDto;
import com.scnsoft.eldermark.mobile.dto.UserDto;
import com.scnsoft.eldermark.mobile.dto.auth.password.PasswordChangeDto;
import com.scnsoft.eldermark.mobile.dto.auth.password.PasswordComplexityRuleDto;
import com.scnsoft.eldermark.mobile.dto.auth.password.PasswordResetDto;
import com.scnsoft.eldermark.mobile.dto.auth.password.PasswordResetRequestDto;
import com.scnsoft.eldermark.mobile.facade.AuthFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthFacade authFacade;

    @PostMapping(path = "/login")
    public Response<UserDto> login(@Valid @RequestBody LoginDto loginDto) {
        return Response.successResponse(authFacade.login(loginDto));
    }

    @PostMapping(path = "/login-by-token")
    public Response<RoomSidUserDto> loginByRoomToken(@Valid @RequestBody RoomTokenLoginDto roomTokenLoginDto) {
        return Response.successResponse(authFacade.loginByRoomToken(roomTokenLoginDto));
    }

    @PostMapping(value = "/password/reset-request")
    public Response<?> requestPasswordReset(@Valid @RequestBody PasswordResetRequestDto passwordResetRequestDto) {
        authFacade.requestPasswordReset(passwordResetRequestDto);
        return Response.successResponse();
    }

    @PostMapping(value = "/password/reset")
    public Response<?> resetPassword(@Valid @RequestBody PasswordResetDto dto) {
        authFacade.resetPassword(dto);
        return Response.successResponse();
    }

    //auth.bad.credentials if incorrect password
    //password.complexity.validation.failure if complexity
    @PostMapping(value = "/password/change")
    public Response<?> changePassword(@Valid @RequestBody PasswordChangeDto dto) {
        authFacade.changePassword(dto);
        return Response.successResponse();
    }

    //    api error password.complexity.validation.failure
    @GetMapping(value = "/password/complexity-rules")
    public Response<List<PasswordComplexityRuleDto>> getPasswordComplexityRules(
            @RequestParam(required = false) Long organizationId, @RequestParam(required = false) String companyCode) {
        return Response.successResponse(authFacade.getPasswordComplexityRules(organizationId, companyCode));
    }
}

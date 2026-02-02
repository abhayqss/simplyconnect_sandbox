package com.scnsoft.eldermark.service;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface InvitationService {

    void invite(Long targetEmployeeId);

    void confirmRegistration(String token, String password);

    void confirmRegistrationExternal(String token, String password, String firstName, String lastName);

    void validateInvitationToken(String token);

    void validateInvitationTokenExternal(String token);

    void declineInvitation(String token);

    void expireInvitations();
}
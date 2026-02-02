package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeRequest;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.shared.carecoordination.contacts.NewAccountLinkedDto;
import com.scnsoft.eldermark.shared.carecoordination.service.ConfirmationEmailDto;
import com.scnsoft.eldermark.shared.carecoordination.service.ResetPasswordDto;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author averazub
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 03-Nov-15.
 */
@Transactional
public interface EmployeeRequestService {
    void createInvitationToken(final Employee creator, final Employee target);

    void createInvitationToken(final Resident creator, final Employee target);

    void createInvitationToken(User patient, Employee providerEmployee, User current);

    boolean hasInvitationTokens(final Employee target);

    void createInvitationTokenForAutoCreated(final Database database, final Employee target);

    ResetPasswordDto createInviteDto(String token);

    ResetPasswordDto createResetPasswordDto(final String token);

    NewAccountLinkedDto createNewAccountLinkedDto(final String token);

    ConfirmationEmailDto createConfirmationEmailDto(Employee employee);

    Employee useInviteToken(final ResetPasswordDto dto);

    void useResetPasswordToken(final ResetPasswordDto dto);

    void declineInviteRequest(final String token);

    void declineResetPasswordToken(final String token);

    void createResetPasswordToken(final Employee employee);

    void declineInvitation(EmployeeRequest employeeRequest);

    void expireInvitation(EmployeeRequest employeeRequest);

    EmployeeRequest getInviteToken(final String token);

    void deleteInvitations(Employee employee);

    EmployeeRequest getResetToken(final String token);

    void sendNewInvitation(ResetPasswordDto dto);

    void sendNewInvitation(Long contactId);
}

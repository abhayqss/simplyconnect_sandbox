package com.scnsoft.eldermark.services.mail;

import com.scnsoft.eldermark.entity.inbound.marco.MarcoDocumentEmailDto;
import com.scnsoft.eldermark.entity.inbound.therap.TherapMailNotificationDto;
import com.scnsoft.eldermark.shared.carecoordination.service.*;
import com.scnsoft.eldermark.shared.phr.SectionUpdateRequestVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author averazub
 * @author knetkachev
 * @author phomal
 * @author pzhurba Created by pzhurba on 24-Sep-15.
 */
@Transactional
public interface ExchangeMailService {
    Future<Boolean> sendMail(String to, String subject, String body);

    Future<Boolean> sendMail(String [] to, String subject, String body);

    Future<Boolean> sendInvitation(final InvitationDto inviteDto);

    Future<Boolean> sendInvitationToMobileApp(final InvitationDto inviteDto);

    Future<Boolean> sendInvitationAuto(final InvitationDto inviteDto);

    Future<Boolean> sendResetPassword(final ResetPasswordMailDto resetPasswordMailDto);

    Future<Boolean> sendNotification(final NotificationDto notification, String initials);

    Future<Boolean> sendConfirmation(final ConfirmationEmailDto confirmationDto);

    Future<Boolean> sendPasswordUpdateNotification(final ConfirmationEmailDto confirmationEmailDto);

    Future<Boolean> sendNewOrgNotification(final NewOrgCreatedDto dto);

    Future<Boolean> sendNewCommunityNotification(final NewCommunityCreatedDto dto);

    Future<Boolean> sendAddedAffiliatedOrganizationNotification(AffiliatedOrganizationNotificationDto dto);

    Future<Boolean> sendDeletedAffiliatedOrganizationNotification(AffiliatedOrganizationNotificationDto dto);

    Future<Boolean> sendUpdateSectionDataRequest(SectionUpdateRequestVO requestVO);

    Future<Boolean> sendInvitationToCareTeam(InvitationDto invitationDto);

    Future<Boolean> sendInvitationToCareTeamNewUser(InvitationDto invitationVO);

    Future<Boolean> sendNoteNotification(final NoteNotificationDto notification, String initials);

    Future<Boolean> sendIncidentReport(byte[] pdfBytes, List<String> toEmails, String attachmentName);

    Future<Boolean> sendVideoCallInviationNotification(final InvitationDto  invitationDto);

    Future<Boolean> sendTherapNotification(final TherapMailNotificationDto therapMailNotificationDto);

    Future<Boolean> sendMarcoNotification(final MarcoDocumentEmailDto marcoDocumentEmailDto);
}

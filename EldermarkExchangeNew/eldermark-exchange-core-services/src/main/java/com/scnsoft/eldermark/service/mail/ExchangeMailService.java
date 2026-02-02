package com.scnsoft.eldermark.service.mail;

import com.scnsoft.eldermark.beans.InvitationDto;
import com.scnsoft.eldermark.dto.AppointmentMailDto;
import com.scnsoft.eldermark.dto.RegistrationConfirmationMailDto;
import com.scnsoft.eldermark.dto.ResetPasswordMailDto;
import com.scnsoft.eldermark.dto.notification.*;
import com.scnsoft.eldermark.dto.notification.MAP.MAPNotificationMailDto;
import com.scnsoft.eldermark.dto.notification.MAP.MAPNotificationSecureMailDto;
import com.scnsoft.eldermark.dto.notification.affiliated.AffiliatedRelationshipNotificationMailDto;
import com.scnsoft.eldermark.dto.notification.careteam.CareTeamMemberCancelledInvitationRecipientNotificationDto;
import com.scnsoft.eldermark.dto.notification.careteam.CareTeamMemberCancelledInvitationSenderNotificationDto;
import com.scnsoft.eldermark.dto.notification.careteam.CareTeamMemberExistingUserInviteNotificationDto;
import com.scnsoft.eldermark.dto.notification.careteam.CareTeamMemberNewUserInviteNotificationDto;
import com.scnsoft.eldermark.dto.notification.deactivate.DeactivateEmployeeNotificationMailDto;
import com.scnsoft.eldermark.dto.notification.event.EventNotificationMailDto;
import com.scnsoft.eldermark.dto.notification.event.EventNotificationSecureMailDto;
import com.scnsoft.eldermark.dto.notification.lab.IncidentReportSubmitNotificationMailDto;
import com.scnsoft.eldermark.dto.notification.lab.LabEventNotificationMailDto;
import com.scnsoft.eldermark.dto.notification.lab.LabEventNotificationSecureMailDto;
import com.scnsoft.eldermark.dto.notification.lab.LabResearchTestResultReceivedNotificationMailDto;
import com.scnsoft.eldermark.dto.notification.note.NoteNotificationMailDto;
import com.scnsoft.eldermark.dto.notification.note.NoteNotificationSecureMailDto;
import com.scnsoft.eldermark.dto.notification.referral.ReferralRequestNotificationMailDto;
import com.scnsoft.eldermark.dto.notification.signature.SignatureRequestCancelNotificationMailDto;
import com.scnsoft.eldermark.dto.notification.signature.SignatureRequestNotificationMailDto;
import com.scnsoft.eldermark.service.DirectAccountDetails;

import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Transactional
public interface ExchangeMailService {

    Future<Boolean> sendInvitation(InvitationDto inviteDto);

    Future<Boolean> sendResetPassword(ResetPasswordMailDto dto);

    Future<Boolean> sendAppointment(AppointmentMailDto dto,
                                    DirectAccountDetails directAccountDetails);

    Future<Boolean> sendRegistrationConfirmation(RegistrationConfirmationMailDto dto);

    Future<Boolean> sendExternalRegistrationConfirmation(RegistrationConfirmationMailDto dto);

    Future<Boolean> sendAddedToCareTeamNotification(AddedToCareTeamNotificationDto dto);

    boolean sendEventNotificationAndWait(EventNotificationMailDto eventNotificationMailDto);

    boolean sendLabEventNotificationAndWait(LabEventNotificationMailDto labNotificationMailDto);

    boolean sendMAPEventNotificationAndWait(MAPNotificationMailDto mapNotificationMailDto);

    boolean sendSecureEventNotificationAndWait(EventNotificationSecureMailDto eventNotificationMailDto,
                                               DirectAccountDetails directAccountDetails);

    boolean sendSecureLabEventNotificationAndWait(LabEventNotificationSecureMailDto labEventNotificationSecureMailDto,
                                                  DirectAccountDetails directAccountDetails);

    boolean sendSecureMapNotificationAndWait(MAPNotificationSecureMailDto mapNotificationSecureMailDto,
                                             DirectAccountDetails directAccountDetails);

    boolean sendNoteNotificationAndWait(NoteNotificationMailDto noteNotificationMailDto);

    boolean sendSecureNoteNotificationAndWait(NoteNotificationSecureMailDto noteNotificationMailDto,
                                              DirectAccountDetails directAccountDetails);

    boolean sendReferralRequestNotificationAndWait(ReferralRequestNotificationMailDto referralRequestNotificationMailDto);

    boolean sendLabResearchTestResultReceivedNotificationAndWait(LabResearchTestResultReceivedNotificationMailDto labResultReceiveNotificationMailDto);

    boolean sendReleaseNoteNotificationAndWait(ReleaseNoteEmailNotificationDto releaseNoteEmailNotificationDto);

    Future<Boolean> sendIncidentReport(byte[] pdfBytes, List<String> toEmails, String attachmentName);

    boolean sendIncidentReportSubmitNotificationAndWait(IncidentReportSubmitNotificationMailDto dto);

    boolean sendAffiliatedRelationshipNotificationAndWait(AffiliatedRelationshipNotificationMailDto dto);

    boolean sendDeactivateEmployeeNotificationAndWait(DeactivateEmployeeNotificationMailDto dto);

    Future<Boolean> sendArizonaMatrixMonthlyNotification(ArizonaMatrixMonthlyNotificationDto dto);

    boolean sendSignatureRequestNotificationAndWait(SignatureRequestNotificationMailDto requestNotificationMailDto);

    Future<Boolean> sendSupportTicketSubmittedNotification(SupportTicketSubmittedNotificationDto dto);

    CompletableFuture<Boolean> sendDemoRequestedNotification(DemoRequestSubmittedNotificationDto dto);

    void sendAppointmentFeatureDisableNotification(String toEmail, String fullName, String organizationName);

    boolean sendUpcomingAppointmentNotification(AppointmentEmailNotificationDto dto);

    boolean sendAppointmentUpdatedNotification(AppointmentEmailNotificationDto dto);

    boolean sendAppointmentCanceledNotification(AppointmentEmailNotificationDto dto);

    boolean sendAppointmentCompletedNotification(AppointmentEmailNotificationDto dto);

    void sendCancelSignatureRequestNotification(SignatureRequestCancelNotificationMailDto dto);

    void sendSimpleEmail(List<String> toEmails, String subject, String message);

    boolean sendInviteNewFamilyMemberNotification(CareTeamMemberNewUserInviteNotificationDto dto);

    boolean sendExistingFamilyMemberInvitationNotification(CareTeamMemberExistingUserInviteNotificationDto dto);

    boolean sendFamilyMemberInvitationCancelledSenderNotification(CareTeamMemberCancelledInvitationSenderNotificationDto dto);

    boolean sendFamilyMemberInvitationCancelledRecipientNotification(CareTeamMemberCancelledInvitationRecipientNotificationDto dto);
}

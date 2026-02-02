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
import com.scnsoft.eldermark.util.DataUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class ExchangeMailServiceImpl implements ExchangeMailService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a z")
            .withZone(TimeZone.getTimeZone("CST6CDT").toZoneId());

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            .withZone(TimeZone.getTimeZone("CST6CDT").toZoneId());

    private static final DateTimeFormatter TIME_FROM_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a")
            .withZone(TimeZone.getTimeZone("CST6CDT").toZoneId());

    private static final DateTimeFormatter TIME_TO_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a z")
            .withZone(TimeZone.getTimeZone("CST6CDT").toZoneId());


    @Value("${mail.from}")
    private String from;

    @Value("${mail.replyTo}")
    private String replyTo;

    @Value("classpath:images/simply-connect-logo.png")
    private Resource logoImage;

    @Value("classpath:images/email/bottom_left.png")
    private Resource emailBottomLeftImage;

    @Value("classpath:images/email/bottom_right.png")
    private Resource emailBottomRightImage;

    @Value("classpath:images/email/dark_left.png")
    private Resource emailDarkLineLeftImage;

    @Value("classpath:images/email/dark_right.png")
    private Resource emailDarkLineRightImage;

    @Value("classpath:images/email/app-store-badge.png")
    private Resource appStoreBadge;

    @Value("classpath:images/email/google-play-badge.png")
    private Resource googlePlayBadge;

    @Value("${app.store.url}")
    private String appStoreUrl;

    @Value("${google.play.url}")
    private String googlePlayUrl;

    @Value("${portal.url}")
    private String portalUrl;

    @Value("${portal.home.externalProvider.url}")
    private String externalProviderHomeUrl;

    @Value("${deactivate.employee.prior.email.notification.minutes}")
    private long deactivateEmployeePriorEmailNotificationPeriodMinutes;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private DirectMessagesService directMessagesService;

    private static final Logger logger = LoggerFactory.getLogger(ExchangeMailServiceImpl.class);
    private static final String EVENT_SUBJECT_TEMPLATE = "A new %s event has been logged to the Simply Connect platform for %s";
    private static final String EVENT_LAB_SUBJECT_TEMPLATE = " COVID-19 results for %s";
    private static final String MAP_SUBJECT_TEMPLATE = "New Medication Action Plan for %s";
    private static final String NOTE_SUBJECT_TEMPLATE = "A note has been %s the Simply Connect platform for %s";


    @Override
    @Async
    public Future<Boolean> sendInvitation(final InvitationDto inviteDto) {
        try {
            logger.info("Current thread {}", Thread.currentThread().getId());
            logger.info("Preparing invitation email to: {}", inviteDto.getToEmail());
            final MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();
                model.put("invitation", inviteDto);
                model.put("portalUrl", portalUrl);
                model.put("buttonUrl", inviteDto.getButtonUrl());
                model.put("buttonLabel", "Create Account");
                createMimeMessageHelper(mimeMessage, inviteDto.getToEmail(),
                        "You've been invited to join the Simply Connect platform", model, "inviteEmail.vm",
                        MobileButtonsMode.DONT_DISPLAY);
            };
            logger.info("Trying to send invitation email to: {}", inviteDto.getToEmail());
            mailSender.send(preparator);
            logger.info("Invitation email has been sent to: {}", inviteDto.getToEmail());
            return new AsyncResult<>(true);
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return new AsyncResult<>(false);
        }
    }

    @Override
    @Async
    public Future<Boolean> sendResetPassword(final ResetPasswordMailDto dto) {
        try {
            final MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();
                model.put("dto", dto);
                model.put("buttonUrl", dto.getUrl());
                model.put("buttonLabel", "Reset Password");
                createMimeMessageHelper(mimeMessage, dto.getToEmail(), "Simply Connect Password Reminder", model,
                        "resetPasswordEmail.vm", MobileButtonsMode.DONT_DISPLAY);
            };
            mailSender.send(preparator);
            return new AsyncResult<>(true);
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return new AsyncResult<>(false);
        }
    }

    @Override
    @Async
    public Future<Boolean> sendAppointment(AppointmentMailDto dto, DirectAccountDetails directAccountDetails) {
        DateTimeFormatter currentDateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a z")
                .withZone(TimeZone.getTimeZone("CST").toZoneId());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.of("UTC"));

        try {

            Map<String, Object> model = new HashMap<>();
            model.put("logoBase64", Base64.encodeBase64String(IOUtils.toByteArray(logoImage.getInputStream())));

            var subject = "A new appointment has been requested via Simply Connect app";
            model.put("subject", subject);

            model.put("dto", dto);
            model.put("requestDateTime", currentDateTimeFormatter.format(Instant.now()));

            if (dto.getServices() != null) {
                model.put("services", StringUtils.join(dto.getServices(), ", ").replaceAll("(?!\\s)\\W", "$0 "));
            }
            model.put("appointmentDate", dateFormatter.format(Instant.ofEpochMilli(dto.getAppointmentDate())));

            String text = mergeTemplateIntoString(velocityEngine, "velocity/newMarketplaceAppointmentSecureMessage.vm",
                    "UTF-8", model);
            List<String> emailAddresses = new ArrayList<String>();
            emailAddresses.add(dto.getToEmail());
            directMessagesService.sendMessage(emailAddresses,
                    subject, text, null, directAccountDetails);

        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return new AsyncResult<>(false);
        }
        return new AsyncResult<>(true);
    }

    @Override
    @Async
    public Future<Boolean> sendRegistrationConfirmation(RegistrationConfirmationMailDto dto) {
        try {
            final MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();
                model.put("dto", dto);
                model.put("portalUrl", portalUrl);
                model.put("buttons", List.of(
                        new EmailButtonDto("Sign in", portalUrl),
                        new EmailButtonDto("Reset password", dto.getPasswordResetUrl())
                ));
                createMimeMessageHelper(mimeMessage, dto.getToEmail(), "Welcome to the Simply Connect platform", model,
                        "registrationConfirmationEmail.vm", MobileButtonsMode.DONT_DISPLAY);
            };
            mailSender.send(preparator);

            return new AsyncResult<>(true);
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return new AsyncResult<>(false);
        }
    }

    @Override
    @Async
    public Future<Boolean> sendExternalRegistrationConfirmation(RegistrationConfirmationMailDto dto) {
        try {
            final MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();
                model.put("dto", dto);
                model.put("portalUrl", externalProviderHomeUrl);
                model.put("buttons", List.of(
                        new EmailButtonDto("Sign in", externalProviderHomeUrl),
                        new EmailButtonDto("Reset password", dto.getPasswordResetUrl())
                ));
                createMimeMessageHelper(mimeMessage, dto.getToEmail(), "Welcome to the Simply Connect platform", model,
                        "externalRegistrationConfirmationEmail.vm", MobileButtonsMode.DONT_DISPLAY);
            };
            mailSender.send(preparator);

            return new AsyncResult<>(true);
        } catch (Exception e) {
            logger.error("Error while sending Email to external user", e);
            return new AsyncResult<>(false);
        }
    }

    @Override
    @Async
    public Future<Boolean> sendAddedToCareTeamNotification(AddedToCareTeamNotificationDto dto) {
        try {
            logger.info("Current thread {}", Thread.currentThread().getId());
            logger.info("Preparing event notification email for {} ", dto.getToEmail());
            mailSender.send(mimeMessage -> {
                var model = new HashMap<String, Object>();
                model.put("fullName", dto.getFullName());
                model.put("buttonLabel", "Sign in");
                model.put("buttonUrl", portalUrl);
                createMimeMessageHelper(
                        mimeMessage,
                        dto.getToEmail(),
                        "You have been connected with the care team",
                        model,
                        "addedToCareTeamNotificationEmail.vm",
                        MobileButtonsMode.DONT_DISPLAY
                );
            });

            return new AsyncResult<>(true);
        } catch (Exception e) {
            logger.error("Error while sending Email", e);
            return new AsyncResult<>(false);
        }
    }

    @Override
    public boolean sendEventNotificationAndWait(EventNotificationMailDto eventNotificationMailDto) {
        try {
            logger.info("Current thread {}", Thread.currentThread().getId());
            logger.info("Preparing event notification email for {} ", eventNotificationMailDto.getReceiverEmail());
            final MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();

                model.put("dto", eventNotificationMailDto);
                model.put("buttonUrl", eventNotificationMailDto.getEventUrl());
                model.put("buttonLabel", "View Event");

                var subject = String.format(EVENT_SUBJECT_TEMPLATE,
                        eventNotificationMailDto.getEventGroup(),
                        eventNotificationMailDto.getClientName());

                createMimeMessageHelper(mimeMessage,
                        eventNotificationMailDto.getReceiverEmail(),
                        subject,
                        model,
                        "event/eventNotificationEmail.vm",
                        MobileButtonsMode.DONT_DISPLAY);
            };
            mailSender.send(preparator);
            return true;
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
    }

    @Override
    public boolean sendLabEventNotificationAndWait(LabEventNotificationMailDto labNotificationMailDto) {
        try {
            logger.info("Current thread {}", Thread.currentThread().getId());
            logger.info("Preparing event notification email for {} ", labNotificationMailDto.getReceiverEmail());
            final MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();

                model.put("dto", labNotificationMailDto);
                model.put("buttonUrl", labNotificationMailDto.getLabOrderUrl());
                model.put("buttonLabel", "View Result");

                var subject = String.format(EVENT_LAB_SUBJECT_TEMPLATE,
                        labNotificationMailDto.getClientName());

                createMimeMessageHelper(mimeMessage,
                        labNotificationMailDto.getReceiverEmail(),
                        subject,
                        model,
                        "event/eventLabNotificationEmail.vm",
                        MobileButtonsMode.DONT_DISPLAY);
            };
            mailSender.send(preparator);
            return true;
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
    }

    @Override
    public boolean sendMAPEventNotificationAndWait(MAPNotificationMailDto mapNotificationMailDto) {
        try {
            logger.info("Current thread {}", Thread.currentThread().getId());
            logger.info("Preparing event notification email for {} ", mapNotificationMailDto.getReceiverEmail());
            final MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();

                model.put("dto", mapNotificationMailDto);
                model.put("buttonUrl", mapNotificationMailDto.getMapUrl());
                model.put("buttonLabel", "View MAP");

                var subject = String.format(MAP_SUBJECT_TEMPLATE,
                        mapNotificationMailDto.getClientName());

                createMimeMessageHelper(mimeMessage,
                        mapNotificationMailDto.getReceiverEmail(),
                        subject,
                        model,
                        "MAP/MAPNotificationEmail.vm",
                        MobileButtonsMode.DONT_DISPLAY);
            };
            mailSender.send(preparator);
            return true;
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
    }

    @Override
    public boolean sendSecureEventNotificationAndWait(EventNotificationSecureMailDto eventNotificationMailDto, DirectAccountDetails directAccountDetails) {
        try {
            var subject = String.format(EVENT_SUBJECT_TEMPLATE,
                    eventNotificationMailDto.getEventGroup(),
                    eventNotificationMailDto.getClientName());

            Map<String, Object> model = new HashMap<>();

            model.put("dto", eventNotificationMailDto);
            model.put("subject", subject);
            model.put("buttonUrl", eventNotificationMailDto.getEventUrl());
            model.put("buttonLabel", "View Event");

            String text = createSecureMessageText(velocityEngine, model, "event/eventNotificationSecureEmail.vm");

            directMessagesService.sendMessage(Collections.singletonList(eventNotificationMailDto.getReceiverEmail()),
                    subject, text, null, directAccountDetails);

        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean sendSecureLabEventNotificationAndWait(LabEventNotificationSecureMailDto labEventNotificationSecureMailDto,
                                                         DirectAccountDetails directAccountDetails) {
        try {
            var subject = String.format(EVENT_LAB_SUBJECT_TEMPLATE, labEventNotificationSecureMailDto.getClientName());

            Map<String, Object> model = new HashMap<>();

            model.put("dto", labEventNotificationSecureMailDto);
            model.put("subject", subject);

            String text = createSecureMessageText(velocityEngine, model, "event/eventLabNotificationSecureEmail.vm");

            directMessagesService.sendMessage(Collections.singletonList(labEventNotificationSecureMailDto.getReceiverEmail()),
                    subject, text, labEventNotificationSecureMailDto.getAttachments(), directAccountDetails);

        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean sendSecureMapNotificationAndWait(MAPNotificationSecureMailDto mapNotificationSecureMailDto, DirectAccountDetails directAccountDetails) {
        try {
            var subject = String.format(MAP_SUBJECT_TEMPLATE, mapNotificationSecureMailDto.getClientName());

            Map<String, Object> model = new HashMap<>();

            model.put("dto", mapNotificationSecureMailDto);
            model.put("subject", subject);
            model.put("buttonUrl", mapNotificationSecureMailDto.getMapUrl());
            model.put("buttonLabel", "View MAP");

            String text = createSecureMessageText(velocityEngine, model, "MAP/MAPNotificationSecureEmail.vm");

            directMessagesService.sendMessage(Collections.singletonList(mapNotificationSecureMailDto.getReceiverEmail()),
                    subject, text, Collections.singletonList(mapNotificationSecureMailDto.getMapPdf()), directAccountDetails);

        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean sendNoteNotificationAndWait(NoteNotificationMailDto noteNotificationMailDto) {
        try {
            final MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();

                model.put("dto", noteNotificationMailDto);
                model.put("buttonUrl", noteNotificationMailDto.getNoteUrl());
                model.put("buttonLabel", "View Note");

                var subject = String.format(NOTE_SUBJECT_TEMPLATE,
                        noteNotificationMailDto.getAction(),
                        noteNotificationMailDto.getClientName());

                createMimeMessageHelper(mimeMessage,
                        noteNotificationMailDto.getReceiverEmail(),
                        subject,
                        model,
                        "note/noteNotificationEmail.vm",
                        MobileButtonsMode.DONT_DISPLAY);
            };
            mailSender.send(preparator);
            return true;
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
    }

    @Override
    public boolean sendSecureNoteNotificationAndWait(NoteNotificationSecureMailDto noteNotificationMailDto, DirectAccountDetails directAccountDetails) {
        try {
            var subject = String.format(NOTE_SUBJECT_TEMPLATE,
                    noteNotificationMailDto.getAction(),
                    noteNotificationMailDto.getClientName());

            Map<String, Object> model = new HashMap<>();

            model.put("dto", noteNotificationMailDto);
            model.put("subject", subject);
            model.put("buttonUrl", noteNotificationMailDto.getNoteUrl());
            model.put("buttonLabel", "View Note");
            model.put("timeFromFormatter", TIME_FROM_FORMATTER);
            model.put("timeToFormatter", TIME_TO_FORMATTER);

            String text = createSecureMessageText(velocityEngine, model, "note/noteNotificationSecureEmail.vm");

            directMessagesService.sendMessage(Collections.singletonList(noteNotificationMailDto.getReceiverEmail()),
                    subject, text, null, directAccountDetails);

        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean sendReferralRequestNotificationAndWait(ReferralRequestNotificationMailDto dto) {
        try {
            final MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();

                model.put("dto", dto);
                model.put("buttonUrl", dto.getUrl());
                model.put("buttonLabel", "View Request");
                createMimeMessageHelper(mimeMessage,
                        dto.getEmail(),
                        dto.getSubject(),
                        model,
                        dto.getTemplateFile(),
                        MobileButtonsMode.DONT_DISPLAY);
            };
            mailSender.send(preparator);
            return true;
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
    }

    @Override
    public boolean sendLabResearchTestResultReceivedNotificationAndWait(LabResearchTestResultReceivedNotificationMailDto dto) {
        try {
            MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();
                model.put("dto", dto);
                model.put("buttonUrl", dto.getUrl());
                model.put("buttonLabel", "View Result");
                createMimeMessageHelper(mimeMessage,
                        dto.getReceiverEmail(),
                        dto.getSubject(),
                        model,
                        dto.getTemplateFile(),
                        MobileButtonsMode.DONT_DISPLAY);
            };
            mailSender.send(preparator);
            return true;
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
    }

    @Override
    public boolean sendReleaseNoteNotificationAndWait(ReleaseNoteEmailNotificationDto dto) {
        try {
            final MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();

                model.put("dto", dto);
                model.put("buttonUrl", dto.getUrl());
                model.put("buttonLabel", "View Full Release Notes");
                createMimeMessageHelper(mimeMessage,
                        dto.getReceiverEmail(),
                        "New features have been released",
                        model,
                        "releaseNoteEmailNotification.vm",
                        MobileButtonsMode.DONT_DISPLAY);
            };
            mailSender.send(preparator);
            return true;
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
    }

    @Override
    @Async
    public Future<Boolean> sendIncidentReport(final byte[] pdfBytes, final List<String> toEmails,
                                              final String attachmentName) {
        try {
            logger.info("Trying send Email");

            final DataSource dataSource = new ByteArrayDataSource(pdfBytes, "application/pdf");
            MimeBodyPart pdfBodyPart = new MimeBodyPart();
            pdfBodyPart.setDataHandler(new DataHandler(dataSource));
            MimeMultipart mimeMultipart = new MimeMultipart();
            mimeMultipart.addBodyPart(pdfBodyPart);

            if (CollectionUtils.isNotEmpty(toEmails)) {
                for (final String toEmail : toEmails) {
                    final MimeMessagePreparator preparator = mimeMessage -> {
                        Map<String, Object> model = new HashMap<String, Object>();
                        MimeMessageHelper message = createMimeMessageHelper(mimeMessage, toEmail, "Incident report",
                                model, "incidentReport.vm", MobileButtonsMode.DONT_DISPLAY);
                        message.addAttachment(attachmentName, dataSource);
                    };
                    mailSender.send(preparator);
                    logger.info("Email to " + toEmail + " was sent");
                }
            }
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return new AsyncResult<>(false);
        }
        return new AsyncResult<>(true);
    }

    @Override
    public boolean sendIncidentReportSubmitNotificationAndWait(IncidentReportSubmitNotificationMailDto dto) {
        try {
            MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();
                model.put("dto", dto);
                model.put("buttonUrl", dto.getUrl());
                model.put("buttonLabel", "View Report");
                createMimeMessageHelper(mimeMessage,
                        dto.getReceiverEmail(),
                        dto.getSubject(),
                        model,
                        dto.getTemplateFile(),
                        MobileButtonsMode.DONT_DISPLAY);
            };
            mailSender.send(preparator);
            return true;
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
    }

    @Override
    public boolean sendAffiliatedRelationshipNotificationAndWait(AffiliatedRelationshipNotificationMailDto dto) {
        String subject;
        String templateFile;
        if (dto.isTerminated()) {
            subject = "Affiliate relationship has been terminated";
            templateFile = "affiliated/TerminatedAffiliatedRelationshipNotification.vm";
        } else {
            subject = dto.getAffiliatedOrganizationName() + " added as affiliated organization with " + dto.getPrimaryOrganizationName();
            templateFile = "affiliated/AddedAffiliatedRelationshipNotification.vm";
        }

        try {
            MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();
                model.put("dto", dto);
                createMimeMessageHelper(mimeMessage,
                        dto.getReceiverEmail(),
                        subject,
                        model,
                        templateFile,
                        MobileButtonsMode.DONT_DISPLAY);
            };
            mailSender.send(preparator);
            return true;
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
    }

    @Override
    public boolean sendDeactivateEmployeeNotificationAndWait(DeactivateEmployeeNotificationMailDto dto) {
        try {
            logger.info("Current thread {}", Thread.currentThread().getId());
            logger.info("Preparing deactivate employee notification email for {} ", dto.getReceiverEmail());
            var priorPeriodDays = Duration.ofMinutes(deactivateEmployeePriorEmailNotificationPeriodMinutes).toDays();
            mailSender.send(mimeMessage -> {
                var model = new HashMap<String, Object>();
                model.put("dto", dto);
                model.put("buttonLabel", "Sign in");
                model.put("buttonUrl", portalUrl);
                createMimeMessageHelper(
                        mimeMessage,
                        dto.getReceiverEmail(),
                        "Your Simply Connect account will be deactivated in " + priorPeriodDays + " days",
                        model,
                        "deactivate/deactivateEmployeeNotification.vm",
                        MobileButtonsMode.DONT_DISPLAY
                );
            });

            return true;
        } catch (Exception e) {
            logger.error("Error while sending Email", e);
            return false;
        }
    }

    @Override
    public boolean sendSignatureRequestNotificationAndWait(SignatureRequestNotificationMailDto dto) {
        try {
            final MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();

                model.put("dto", dto);
                model.put("buttonUrl", dto.getUrl());
                model.put("buttonLabel", "Review Document");
                createMimeMessageHelper(mimeMessage,
                        dto.getEmail(),
                        dto.getSubject(),
                        model,
                        dto.getTemplateFile(),
                        MobileButtonsMode.DONT_DISPLAY);
            };
            mailSender.send(preparator);
            return true;
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
    }

    @Override
    @Async
    public Future<Boolean> sendSupportTicketSubmittedNotification(SupportTicketSubmittedNotificationDto dto) {
        try {
            mailSender.send(mimeMessage -> {
                var helper = createMimeMessageHelper(
                        mimeMessage,
                        dto.getReceiverEmail(),
                        "Simply Connect support ticket # " + dto.getTicketNumber(),
                        new HashMap<>(Map.of(
                                "formattedDate", DATE_TIME_FORMATTER.format(dto.getDate()),
                                "escapedMessage", StringEscapeUtils.escapeHtml(dto.getMessage()),
                                "dto", dto
                        )),
                        "supportTicketSubmittedNotificationEmail.vm",
                        MobileButtonsMode.DONT_DISPLAY
                );

                for (var attachment : dto.getAttachments()) {
                    helper.addAttachment(attachment.getFileName(), attachment.getData(), attachment.getMediaType());
                }
            });
            return AsyncResult.forValue(true);
        } catch (Exception e) {
            logger.error("Error while sending Email", e);
            return AsyncResult.forValue(false);
        }
    }

    @Override
    @Async
    public CompletableFuture<Boolean> sendDemoRequestedNotification(DemoRequestSubmittedNotificationDto dto) {
        try {
            mailSender.send(mimeMessage -> createMimeMessageHelper(
                    mimeMessage,
                    dto.getReceiverEmail(),
                    "Demo request",
                    new HashMap<>(Map.of(
                            "formattedDate", DATE_TIME_FORMATTER.format(dto.getDate()),
                            "dto", dto
                    )),
                    "demoRequestSubmittedNotificationEmail.vm",
                    MobileButtonsMode.DONT_DISPLAY
            ));
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            logger.error("Error while sending Email", e);
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    @Async
    public Future<Boolean> sendArizonaMatrixMonthlyNotification(ArizonaMatrixMonthlyNotificationDto dto) {
        try {
            logger.info("Current thread {}", Thread.currentThread().getId());
            logger.info("Preparing Arizona Matrix Monthly notification email for {}", dto.getEmployeeEmail());
            mailSender.send(mimeMessage -> createMimeMessageHelper(
                    mimeMessage,
                    dto.getEmployeeEmail(),
                    "Arizona Self-Sufficiency Matrix Monthly Report",
                    new HashMap<>(Map.of(
                            "fullName", dto.getEmployeeName(),
                            "reportUrl", dto.getReportUrl()
                    )),
                    "arizonaMatrixMonthlyNotificationEmail.vm",
                    MobileButtonsMode.DONT_DISPLAY
            ));
            return new AsyncResult<>(true);
        } catch (Exception e) {
            logger.error("Error while sending Email", e);
            return new AsyncResult<>(false);
        }
    }

    @Override
    @Async
    public void sendAppointmentFeatureDisableNotification(String toEmail, String fullName, String organizationName) {
        try {
            mailSender.send(mimeMessage -> createMimeMessageHelper(
                    mimeMessage,
                    toEmail,
                    "You will no longer be able to view appointments through Simply Connect",
                    new HashMap<>(Map.of(
                            "fullName", fullName,
                            "organizationName", organizationName
                    )),
                    "appointmentFeatureDisableNotification.vm",
                    MobileButtonsMode.DONT_DISPLAY
            ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private MimeMessageHelper createMimeMessageHelper(MimeMessage mimeMessage, String toEmail, String subject,
                                                      Map<String, Object> model, String templateFile,
                                                      MobileButtonsMode mobileButtonsMode) throws MessagingException {
        model.put("subject", subject);
        model.put("template", templateFile);
        if (mobileButtonsMode != null && mobileButtonsMode.isAddData()) {
            if (mobileButtonsMode == MobileButtonsMode.SHOW_IN_THE_BOTTOM) {
                model.put("mobileButtons", true);
            }
            model.put("appStoreUrl", appStoreUrl);
            model.put("googlePlayUrl", googlePlayUrl);
        }

        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setFrom(from);
        message.setReplyTo(replyTo);
        String text = mergeTemplateIntoString(velocityEngine, "velocity/baseEmail.vm", "UTF-8", model);
        message.setText(text, true);
        message.addInline("logo", logoImage);
        message.addInline("bottomLeftImage", emailBottomLeftImage);
        message.addInline("bottomRightImage", emailBottomRightImage);
        message.addInline("darkLineLeftImage", emailDarkLineLeftImage);
        message.addInline("darkLineRightImage", emailDarkLineRightImage);
        if (mobileButtonsMode != null && mobileButtonsMode.isAddData()) {
            message.addInline("appStore", appStoreBadge);
            message.addInline("googlePlay", googlePlayBadge);
        }
        return message;
    }

    private String mergeTemplateIntoString(VelocityEngine velocityEngine, String templateLocation, String encoding,
                                           Map<String, Object> model) throws VelocityException {

        StringWriter result = new StringWriter();
        VelocityContext velocityContext = new VelocityContext(model);
        velocityEngine.mergeTemplate(templateLocation, encoding, velocityContext, result);
        return result.toString();
    }

    private String createSecureMessageText(VelocityEngine velocityEngine, Map<String, Object> model, String templateLocation) throws IOException {
        model.put("logoBase64", Base64.encodeBase64String(IOUtils.toByteArray(logoImage.getInputStream())));

        model.put("template", templateLocation);

        model.put("dateTimeFormatter", DATE_TIME_FORMATTER);
        model.put("dateFormatter", DATE_FORMATTER);

        model.put("DataUtils", DataUtils.class);
        model.put("Instant", Instant.class);

        return mergeTemplateIntoString(velocityEngine, "velocity/baseSecureEmail.vm",
                "UTF-8", model);

    }

    @Override
    public void sendSimpleEmail(List<String> toEmails, String subject, String message) {
        toEmails.forEach(email -> {
            logger.info("Sending email to {}", email);
            var msg = new SimpleMailMessage();
            msg.setFrom(replyTo);
            msg.setTo(email);
            msg.setSubject(subject);
            msg.setText(message);
            mailSender.send(msg);
        });
    }

    @Override
    public boolean sendUpcomingAppointmentNotification(AppointmentEmailNotificationDto dto) {
        return sendAppointmentEmailNotification(
                "A New Appointment has been scheduled " + dto.getAppointmentDateTime(),
                dto,
                "upcomingAppointmentNotificationEmail.vm"
        );
    }

    @Override
    public boolean sendAppointmentUpdatedNotification(AppointmentEmailNotificationDto dto) {
        return sendAppointmentEmailNotification(
                "Your appointment on " + dto.getAppointmentDateTime() + " has been updated",
                dto,
                "appointmentUpdatedNotificationEmail.vm"
        );
    }

    @Override
    public boolean sendAppointmentCanceledNotification(AppointmentEmailNotificationDto dto) {
        return sendAppointmentEmailNotification(
                "Cancelled appointment",
                dto,
                "appointmentCanceledNotificationEmail.vm"
        );
    }

    @Override
    public boolean sendAppointmentCompletedNotification(AppointmentEmailNotificationDto dto) {
        return sendAppointmentEmailNotification(
                "Completed appointment",
                dto,
                "appointmentCompletedNotificationEmail.vm"
        );
    }

    private boolean sendAppointmentEmailNotification(
            String subject,
            AppointmentEmailNotificationDto dto,
            String templateFile
    ) {
        try {
            mailSender.send(mimeMessage -> {
                Map<String, Object> model = new HashMap<>();
                model.put("dto", dto);
                createMimeMessageHelper(
                        mimeMessage,
                        dto.getReceiverEmail(),
                        subject,
                        model,
                        templateFile,
                        MobileButtonsMode.DONT_DISPLAY
                );
            });
            return true;
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
    }

    @Override
    public void sendCancelSignatureRequestNotification(SignatureRequestCancelNotificationMailDto dto) {
        try {
            final MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();

                model.put("recipientName", dto.getRecipientName());
                model.put("buttonUrl", dto.getUrl());
                model.put("buttonLabel", "View Request");
                createMimeMessageHelper(mimeMessage,
                        dto.getEmail(),
                        dto.getSubject(),
                        model,
                        dto.getTemplateFile(),
                        MobileButtonsMode.DONT_DISPLAY);
            };
            mailSender.send(preparator);
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
        }
    }

    @Override
    public boolean sendInviteNewFamilyMemberNotification(CareTeamMemberNewUserInviteNotificationDto inviteDto) {
        try {
            logger.info("Current thread {}", Thread.currentThread().getId());
            logger.info("Preparing ctm invitation email to: {}", inviteDto.getReceiverEmail());
            final MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();
                model.put("invitation", inviteDto);
                model.put("portalUrl", portalUrl);
                createMimeMessageHelper(mimeMessage, inviteDto.getReceiverEmail(),
                        inviteDto.getSubject(), model,
                        "inviteNewUserCareTeamEmail.vm", MobileButtonsMode.ADD_DATA_TO_MESSAGE);

            };
            logger.info("Trying to send invitation email to: {}", inviteDto.getReceiverEmail());
            mailSender.send(preparator);
            logger.info("Invitation email has been sent to: {}", inviteDto.getReceiverEmail());
            return true;
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
    }

    @Override
    public boolean sendExistingFamilyMemberInvitationNotification(CareTeamMemberExistingUserInviteNotificationDto inviteDto) {
        try {
            logger.info("Current thread {}", Thread.currentThread().getId());
            logger.info("Preparing ctm invitation email to: {}", inviteDto.getReceiverEmail());
            final MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();
                model.put("invitation", inviteDto);
                model.put("portalUrl", portalUrl);
                createMimeMessageHelper(mimeMessage, inviteDto.getReceiverEmail(),
                        inviteDto.getSubject(), model,
                        "inviteExistingUserCareTeamEmail.vm", MobileButtonsMode.DONT_DISPLAY);
            };
            logger.info("Trying to send invitation email to: {}", inviteDto.getReceiverEmail());
            mailSender.send(preparator);
            logger.info("Invitation email has been sent to: {}", inviteDto.getReceiverEmail());
            return true;
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
    }

    @Override
    public boolean sendFamilyMemberInvitationCancelledSenderNotification(CareTeamMemberCancelledInvitationSenderNotificationDto dto) {
        try {
            logger.info("Current thread {}", Thread.currentThread().getId());
            logger.info("Preparing ctm invitation email to: {}", dto.getReceiverEmail());
            final MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();
                model.put("cancelledInvitation", dto);
                model.put("portalUrl", portalUrl);
                createMimeMessageHelper(mimeMessage, dto.getReceiverEmail(),
                        dto.getSubject(), model,
                        "cancelledInvitationSenderCareTeamEmail.vm", MobileButtonsMode.DONT_DISPLAY);
            };
            logger.info("Trying to send invitation email to: {}", dto.getReceiverEmail());
            mailSender.send(preparator);
            logger.info("Invitation email has been sent to: {}", dto.getReceiverEmail());
            return true;
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
    }

    @Override
    public boolean sendFamilyMemberInvitationCancelledRecipientNotification(CareTeamMemberCancelledInvitationRecipientNotificationDto dto) {
        try {
            logger.info("Current thread {}", Thread.currentThread().getId());
            logger.info("Preparing ctm invitation email to: {}", dto.getReceiverEmail());
            final MimeMessagePreparator preparator = mimeMessage -> {
                Map<String, Object> model = new HashMap<>();
                model.put("cancelledInvitation", dto);
                model.put("portalUrl", portalUrl);
                createMimeMessageHelper(mimeMessage, dto.getReceiverEmail(),
                        dto.getSubject(), model,
                        "cancelledInvitationRecipientCareTeamEmail.vm", MobileButtonsMode.DONT_DISPLAY);
            };
            logger.info("Trying to send invitation email to: {}", dto.getReceiverEmail());
            mailSender.send(preparator);
            logger.info("Invitation email has been sent to: {}", dto.getReceiverEmail());
            return true;
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return false;
        }
    }

    private enum MobileButtonsMode {
        SHOW_IN_THE_BOTTOM(true),
        ADD_DATA_TO_MESSAGE(true),
        DONT_DISPLAY(false);

        private final boolean addData;

        MobileButtonsMode(boolean addData) {
            this.addData = addData;
        }

        public boolean isAddData() {
            return addData;
        }
    }
}

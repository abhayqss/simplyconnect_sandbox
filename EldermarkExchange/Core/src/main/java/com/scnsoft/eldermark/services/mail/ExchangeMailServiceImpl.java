package com.scnsoft.eldermark.services.mail;

import com.scnsoft.eldermark.entity.inbound.marco.MarcoDocumentEmailDto;
import com.scnsoft.eldermark.entity.inbound.therap.TherapMailNotificationDto;
import com.scnsoft.eldermark.shared.carecoordination.service.*;
import com.scnsoft.eldermark.shared.phr.SectionUpdateRequestVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static java.lang.String.format;

/**
 * @author averazub
 * @author knetkachev
 * @author phomal
 * @author pzhurba Created by pzhurba on 24-Sep-15.
 */
@Service
public class ExchangeMailServiceImpl implements ExchangeMailService {

    @Value("${mail.from}")
    private String from;

    @Value("${mail.replyTo}")
    private String replyTo;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    @Value("classpath:images/logo-mail.png")

    private Resource logoImage;

    @Value("classpath:images/Download_on_the_App_Store_Badge_US-UK_135x40.png")
    private Resource appStore;

    @Value("classpath:images/google-play-badge.png")
    private Resource googlePlay;

    @Value("${app.store.url}")
    private String appStoreUrl;

    @Value("${google.play.url}")
    private String googlePlayUrl;

    private static final Logger logger = LoggerFactory.getLogger(ExchangeMailServiceImpl.class);

    @Async
    public Future<Boolean> sendMail(String to, String subject, String body) {
        try {
            logger.info("Trying send Email");
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setReplyTo(replyTo);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            logger.info("Email to " + to + " was send");
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return new AsyncResult<>(false);
        }
        return new AsyncResult<>(true);
    }

    @Async
    public Future<Boolean> sendMail(String [] to, String subject, String body) {
        try {
            logger.info("Trying send Email");
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setReplyTo(replyTo);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            logger.info("Email to " + to + " was send");
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return new AsyncResult<Boolean>(false);
        }
        return new AsyncResult<Boolean>(true);
    }

    @Override
    @Async
    public Future<Boolean> sendInvitation(final InvitationDto inviteDto) {
        try {
            final MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("invitation", inviteDto);
                    model.put("buttonUrl", inviteDto.getUrl());
                    model.put("buttonLabel", "New User");
                    createMimeMessageHelper(mimeMessage, inviteDto.getToEmail(), "You've been invited to join Simply Connect system",
                            model, "inviteEmail.vm", false);
                }
            };
            logger.info("Trying send Invitation Email to: " + inviteDto.getToEmail());
            mailSender.send(preparator);

            return new AsyncResult<>(true);
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return new AsyncResult<>(false);
        }
    }

    /**
     * Send email invitation to install mobile app (Recipient: Friend/Family member)
     */
    @Override
    @Async
    public Future<Boolean> sendInvitationToMobileApp(final InvitationDto inviteDto) {
        try {
            final MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("invitation", inviteDto);
                    model.put("mobileButtons", true);
                    createMimeMessageHelper(mimeMessage, inviteDto.getToEmail(), "Access to Simply Connect Mobile Application",
                            model, "inviteEmailToMobileApp.vm", true);
                }
            };
            logger.info("Trying to send Invitation Email (mobile app) to: " + inviteDto.getToEmail());
            mailSender.send(preparator);

            return new AsyncResult<>(true);
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return new AsyncResult<>(false);
        }
    }

    @Override
    @Async
    public Future<Boolean> sendInvitationAuto(final InvitationDto inviteDto) {
        try {
            final MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("invitation", inviteDto);
                    model.put("buttonUrl", inviteDto.getUrl());
                    model.put("buttonLabel", "New User");
                    createMimeMessageHelper(mimeMessage, inviteDto.getToEmail(), "You've been invited to join Simply Connect system",
                            model, "inviteAutoCreatedEmail.vm", false);
                }
            };
            mailSender.send(preparator);

            logger.error("Sending invitation Email to ", inviteDto.getToEmail());
            return new AsyncResult<>(true);
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return new AsyncResult<>(false);
        }
    }

    @Override
    @Async
    public Future<Boolean> sendConfirmation(final ConfirmationEmailDto confirmationDto) {
        try {

            final MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("confirmation", confirmationDto);
                    model.put("buttonUrl", confirmationDto.getResetPasswordUrl());
                    model.put("buttonLabel", "Reset Password");
                    model.put("mobileButtons", true);
                    createMimeMessageHelper(mimeMessage, confirmationDto.getToEmail(), "Welcome to Simply Connect system", model, "confirmationEmail.vm", true);
                }
            };
            mailSender.send(preparator);

            return new AsyncResult<>(true);
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return new AsyncResult<>(false);
        }
    }

    @Override
    public Future<Boolean> sendPasswordUpdateNotification(final ConfirmationEmailDto confirmationDto) {
        try {
            final MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("confirmation", confirmationDto);
                    model.put("buttonUrl", confirmationDto.getResetPasswordUrl());
                    model.put("buttonLabel", "Reset Password");
                    createMimeMessageHelper(mimeMessage, confirmationDto.getToEmail(), "Your password changed", model, "passwordUpdated.vm", false);
                }
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
    public Future<Boolean> sendNotification(final NotificationDto dto, final String initials) {
        try {

            final MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {
                    String subject = format("A new %s event has been logged to the Simply Connect platform %s ", dto.getEventGroup(), initials);
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("notification", dto);
//                    model.put("subject", subject);
//                    model.put("appStoreUrl", appStoreUrl);
//                    model.put("template", );
                    model.put("buttonUrl", dto.getPortalUrl() + "?startPage=care-coordination/events-log&id=" + dto.getEventId() + "&orgId=" + dto.getDatabaseId());
                    model.put("buttonLabel", "Login");
                    createMimeMessageHelper(mimeMessage, dto.getToEmail(), subject, model, "notificationEmail.vm", false);
                }
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
    public Future<Boolean> sendResetPassword(final ResetPasswordMailDto dto) {
        try {

            final MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("dto", dto);
                    model.put("buttonUrl", dto.getUrl());
                    model.put("buttonLabel", "Reset Password");
                    createMimeMessageHelper(mimeMessage, dto.getToEmail(), "Simply Connect Password Reminder", model, "resetPasswordEmail.vm", false);
                }
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
    public Future<Boolean> sendNewOrgNotification(final NewOrgCreatedDto dto) {
        try {
            final MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("newOrgCreated", dto);
                    model.put("buttonUrl", dto.getManageOrgLink());
                    model.put("buttonLabel", "Manage Organizations");
                    model.put("buttonWidth", 200);
                    createMimeMessageHelper(mimeMessage, dto.getToEmail(), dto.getOrgName() + " organization has been created automatically", model, "newOrgCreated.vm", false);
                }
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
    public Future<Boolean> sendNewCommunityNotification(final NewCommunityCreatedDto dto) {
        try {
            final MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("newCommunityCreated", dto);
                    model.put("buttonUrl", dto.getManageComLink());
                    model.put("buttonLabel", "Login");
//                    model.put("buttonWidth", 200);
                    createMimeMessageHelper(mimeMessage, dto.getToEmail(), dto.getCommunityName() + " community has been created automatically", model, "newCommunityCreated.vm", false);
                }
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
    public Future<Boolean> sendAddedAffiliatedOrganizationNotification(final AffiliatedOrganizationNotificationDto dto) {
        try {
            final MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("affOrg", dto);
                    model.put("buttonUrl", dto.getLink());
                    model.put("buttonLabel", "Login");
//                    model.put("buttonWidth", 200);

                    createMimeMessageHelper(mimeMessage, dto.getEmail(), dto.getAffiliatedOrganization() + " added as affiliated organization for " + dto.getPrimaryOrganization(), model, "affiliatedOrganizationAdded.vm", false);
                }
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
    public Future<Boolean> sendDeletedAffiliatedOrganizationNotification(final AffiliatedOrganizationNotificationDto dto) {
        try {
            final MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("affOrg", dto);
                    createMimeMessageHelper(mimeMessage, dto.getEmail(), "Affiliate relationship has been terminated", model, "affiliatedOrganizationDeleted.vm", false);
                }
            };
            mailSender.send(preparator);

            return new AsyncResult<>(true);
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return new AsyncResult<>(false);
        }
    }

    @Override
    public Future<Boolean> sendUpdateSectionDataRequest(final SectionUpdateRequestVO requestVO) {
        try {
            final MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("request", requestVO);
                    MimeMessageHelper message = createMimeMessageHelper(mimeMessage, requestVO.getToEmail(), "CCD Section Update Request", model, "sectionUpdateRequestEmail.vm", false);
                    for (MultipartFile attachment : requestVO.getAttachments()) {
                        message.addAttachment(attachment.getOriginalFilename(), (InputStreamSource) attachment, attachment.getContentType());
                    }
                }
            };
            mailSender.send(preparator);

            return new AsyncResult<>(true);
        } catch (Exception exc) {
            logger.error("Error while sending email ", exc);
            return new AsyncResult<>(false);
        }
    }

    /**
     * Send email invitation to be a part of a care team of a patient (Recipient: Medical staff (invited person))
     */
    @Override
    public Future<Boolean> sendInvitationToCareTeam(final InvitationDto invitationVO) {
        try {
            final MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("invitation", invitationVO);
                    model.put("buttonUrl", invitationVO.getUrl());
                    model.put("buttonLabel", "Accept or reject");
                    model.put("buttonWidth", 250);
                    createMimeMessageHelper(mimeMessage, invitationVO.getToEmail(),"You've been invited to be a part of the care team" ,model, "invitationToCareTeam.vm",
                            false);
                }
            };
            mailSender.send(preparator);

            return new AsyncResult<>(true);
        } catch (Exception exc) {
            logger.error("Error while sending email ", exc);
            return new AsyncResult<>(false);
        }
    }

    /**
     * Send email invitation to be a part of a care team of a patient (Recipient: Friend/Family member (invited person))
     */
    @Override
    public Future<Boolean> sendInvitationToCareTeamNewUser(final InvitationDto invitationVO) {
        try {
            final MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("invitation", invitationVO);
                    model.put("buttonUrl", invitationVO.getUrl());
                    model.put("buttonLabel", "New User");
                    createMimeMessageHelper(mimeMessage, invitationVO.getToEmail(), "You've been invited to join Simply Connect system",
                            model, "invitationToCareTeamNewUser.vm", false);
                }
            };
            mailSender.send(preparator);

            return new AsyncResult<>(true);
        } catch (Exception exc) {
            logger.error("Error while sending email ", exc);
            return new AsyncResult<>(false);
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
                    final MimeMessagePreparator preparator = new MimeMessagePreparator() {
                        public void prepare(MimeMessage mimeMessage) throws Exception {
                            Map<String, Object> model = new HashMap<String, Object>();
                            MimeMessageHelper message = createMimeMessageHelper(mimeMessage, toEmail, "Incident report", model, "incidentReport.vm",false);
                            message.addAttachment(attachmentName, dataSource);
                        }
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

    private MimeMessageHelper createMimeMessageHelper(MimeMessage mimeMessage, String toEmail, String subject,
            Map<String, Object> model, String templateFile, boolean mobileButtons) throws MessagingException {
        model.put("subject", subject);
        model.put("template", templateFile);
        if (mobileButtons) {
            model.put("appStoreUrl", appStoreUrl);
            model.put("googlePlayUrl", googlePlayUrl);
        }

        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setFrom(from);
        message.setReplyTo(replyTo);

        String text = VelocityEngineUtils.mergeTemplateIntoString(
                velocityEngine, "velocity/baseEmail.vm", "UTF-8", model);
        message.setText(text, true);
        message.addInline("logo", logoImage);
        if (mobileButtons) {
            message.addInline("appStore", appStore);
            message.addInline("googlePlay", googlePlay);
        }
        return message;
    }

    @Override
    @Async
    public Future<Boolean> sendNoteNotification(final NoteNotificationDto notification, final String initials) {
        try {

            final MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {
                    String subject = "A note has been " + notification.getAction() + " the Simply Connect system" + initials;
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("notification", notification);
                    model.put("buttonUrl", notification.getPortalUrl() + "?startPage=care-coordination/patients&note=" +
                            notification.getNoteId() + "&patient=" + notification.getPatientId());
                    model.put("buttonLabel", "Login");
                    createMimeMessageHelper(mimeMessage, notification.getToEmail(), subject, model, "noteEmailNotification.vm", false);
                }
            };
            mailSender.send(preparator);

            return new AsyncResult<Boolean>(true);
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return new AsyncResult<>(false);
        }
    }

    @Override
    @Async
    public Future<Boolean> sendTherapNotification(final TherapMailNotificationDto therapMailNotificationDto) {
        try {

            final MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {
                    String subject = "Results of processing data from Events/IDFs files";
                    Map<String, Object> model = new HashMap<>();
                    model.put("notification", therapMailNotificationDto);
                    final MimeMessageHelper message = createMimeMessageHelper(mimeMessage,
                            therapMailNotificationDto.getRecipientEmail(), subject, model, "therapNotification.vm",
                            false);
                    message.addAttachment("error_report.txt",
                            new ByteArrayResource(therapMailNotificationDto.getErrorReport()));
                }
            };
            mailSender.send(preparator);

            return new AsyncResult<>(true);
        } catch (Exception e) {
            logger.error("Error while sending Therap notification email ", e);
            return new AsyncResult<>(false);
        }
    }

    @Override
    public Future<Boolean> sendMarcoNotification(final MarcoDocumentEmailDto marcoDocumentEmailDto) {
        try {

            final MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {
                    String subject = marcoDocumentEmailDto.getSubject();
                    Map<String, Object> model = new HashMap<>();
                    model.put("notification", marcoDocumentEmailDto);
                    createMimeMessageHelper(mimeMessage, marcoDocumentEmailDto.getToEmail(), subject, model,
                            "marcoNotification.vm", false);
                }
            };
            mailSender.send(preparator);

            return new AsyncResult<>(true);
        } catch (Exception e) {
            logger.error("Error while sending Marco notification email ", e);
            return new AsyncResult<>(false);
        }
    }

    @Override
    @Async
    public Future<Boolean> sendVideoCallInviationNotification(final InvitationDto  invitationDto) {
        try {
            final MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {
                    String subject = "Invitation to join Simply Connect system"                          ;
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("invitationBy", invitationDto.getCreator());
                    model.put("invitationTo", invitationDto.getCareReceiver());
                    model.put("appStoreUrl", appStoreUrl);
                    model.put("googlePlayUrl", googlePlayUrl);
                    createMimeMessageHelper(mimeMessage, invitationDto.getToEmail(), subject, model,
                            "inviteToVideoCall.vm", true);
                }
            };
            mailSender.send(preparator);

            return new AsyncResult<Boolean>(true);
        } catch (Exception e) {
            logger.error("Error while sending Email ", e);
            return new AsyncResult<Boolean>(false);
        }
    }
}

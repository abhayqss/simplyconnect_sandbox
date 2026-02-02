package com.scnsoft.eldermark.beans.audit;

import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.entity.note.NoteType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.beans.audit.AuditLogActivityGroup.*;

public enum AuditLogActivity {
    LOG_IN(0L, LOG_IN_OUT, "Log in", new AuditLogActionWithParams(AuditLogAction.LOG_IN, AuditLogType.LOGIN_LOGOUT)),
    LOG_OUT(1L, LOG_IN_OUT, "Log out", new AuditLogActionWithParams(AuditLogAction.LOG_OUT, AuditLogType.LOGIN_LOGOUT)),
    PASSWORD_RESET(2L, LOG_IN_OUT, "Password reset", new AuditLogActionWithParams(AuditLogAction.PASSWORD_RESET, AuditLogType.LOGIN_LOGOUT)),

    CLIENT_CREATE(3L, CLIENT_PROFILE, "Create client record", new AuditLogActionWithParams(AuditLogAction.CLIENT_CREATE, AuditLogType.CLIENT_PROFILE)),
    CLIENT_UPDATE(4L, CLIENT_PROFILE, "Update client record", new AuditLogActionWithParams(AuditLogAction.CLIENT_UPDATE, AuditLogType.CLIENT_PROFILE)),
    CLIENT_ACTIVATE(82L, CLIENT_PROFILE, "Activate record", new AuditLogActionWithParams(AuditLogAction.CLIENT_ACTIVATE, AuditLogType.CLIENT_PROFILE)),
    CLIENT_DEACTIVATE(83L, CLIENT_PROFILE, "Deactivate record", new AuditLogActionWithParams(AuditLogAction.CLIENT_DEACTIVATE, AuditLogType.CLIENT_PROFILE)),
    CLIENT_VIEW_LISTING(5L, CLIENT_PROFILE, "View client listing", new AuditLogActionWithParams(AuditLogAction.CLIENT_VIEW_LISTING, AuditLogType.CLIENT_PROFILE)),
    CLIENT_VIEW(6L, CLIENT_PROFILE, "View client profile", new AuditLogActionWithParams(AuditLogAction.CLIENT_VIEW, AuditLogType.CLIENT_PROFILE)),
    CLIENT_VIEW_CALL_HISTORY(114L, CLIENT_PROFILE, "View call history", new AuditLogActionWithParams(AuditLogAction.CALL_VIEW_LISTING, AuditLogType.CLIENT_PROFILE)),
    CLIENT_CREATE_SC_USER_FROM_CLIENT(115L, CLIENT_PROFILE, "Create a user account from Client record", new AuditLogActionWithParams(AuditLogAction.CREATE_ASSOCIATED_SC_USER, AuditLogType.CLIENT_PROFILE)),
    MEDICATION_ADD(116L, CLIENT_PROFILE, "Add medication", new AuditLogActionWithParams(AuditLogAction.MEDICATION_CREATE, AuditLogType.CLIENT_PROFILE)),
    MEDICATION_EDIT(117L, CLIENT_PROFILE, "Edit medication", new AuditLogActionWithParams(AuditLogAction.MEDICATION_EDIT, AuditLogType.CLIENT_PROFILE)),
    MEDICATION_VIEW(7L, CLIENT_PROFILE, "View medication", new AuditLogActionWithParams(AuditLogAction.MEDICATION_VIEW, AuditLogType.MEDICATION)),
    PROBLEM_VIEW(8L, CLIENT_PROFILE, "View problem", new AuditLogActionWithParams(AuditLogAction.PROBLEM_VIEW, AuditLogType.PROBLEM)),
    ALLERGY_VIEW(9L, CLIENT_PROFILE, "View allergy", new AuditLogActionWithParams(AuditLogAction.ALLERGY_VIEW, AuditLogType.ALLERGY)),

    RIDE_REQUEST(10L, TRANSPORTATION, "Request a ride", new AuditLogActionWithParams(AuditLogAction.RIDE_REQUEST, AuditLogType.TRANSPORTATION)),
    RIDE_HISTORY(11L, TRANSPORTATION, "Ride history", new AuditLogActionWithParams(AuditLogAction.RIDE_HISTORY, AuditLogType.TRANSPORTATION)),

    SERVICE_PLAN_VIEW_LISTING(12L, SERVICE_PLAN, "View service plans", new AuditLogActionWithParams(AuditLogAction.SERVICE_PLAN_VIEW_LISTING, AuditLogType.SERVICE_PLAN)),
    SERVICE_PLAN_VIEW(13L, SERVICE_PLAN, "View service plan", new AuditLogActionWithParams(AuditLogAction.SERVICE_PLAN_VIEW, AuditLogType.SERVICE_PLAN)),
    SERVICE_PLAN_UPDATE(14L, SERVICE_PLAN, "Update service plan", new AuditLogActionWithParams(AuditLogAction.SERVICE_PLAN_UPDATE, AuditLogType.SERVICE_PLAN)),
    SERVICE_PLAN_DOWNLOAD(15L, SERVICE_PLAN, "Download service plan", new AuditLogActionWithParams(AuditLogAction.SERVICE_PLAN_DOWNLOAD, AuditLogType.SERVICE_PLAN)),
    SERVICE_PLAN_CREATE(16L, SERVICE_PLAN, "Create service plan", new AuditLogActionWithParams(AuditLogAction.SERVICE_PLAN_CREATE, AuditLogType.SERVICE_PLAN)),

    ASSESSMENT_VIEW_LISTING(17L, ASSESSMENT, "View assessments", new AuditLogActionWithParams(AuditLogAction.ASSESSMENT_VIEW_LISTING, AuditLogType.ASSESSMENT)),
    ASSESSMENT_VIEW(88L, ASSESSMENT, "View assessment", new AuditLogActionWithParams(AuditLogAction.ASSESSMENT_VIEW, AuditLogType.ASSESSMENT)),
    ASSESSMENT_CREATE(89L, ASSESSMENT, "Create assessment", new AuditLogActionWithParams(AuditLogAction.ASSESSMENT_CREATE, AuditLogType.ASSESSMENT)),
    ASSESSMENT_DOWNLOAD(90L, ASSESSMENT, "Download assessment", new AuditLogActionWithParams(AuditLogAction.ASSESSMENT_DOWNLOAD, AuditLogType.ASSESSMENT)),
    ASSESSMENT_EDIT(91L, ASSESSMENT, "Edit assessment", new AuditLogActionWithParams(AuditLogAction.ASSESSMENT_EDIT, AuditLogType.ASSESSMENT)),

    DOCUMENT_VIEW_LISTING(30L, DOCUMENT, "View documents", new AuditLogActionWithParams(AuditLogAction.DOCUMENT_VIEW_LISTING, AuditLogType.DOCUMENT)),
    DOCUMENT_DOWNLOAD(31L, DOCUMENT, "Download document", new AuditLogActionWithParams(AuditLogAction.DOCUMENT_DOWNLOAD, AuditLogType.DOCUMENT)),
    DOCUMENT_VIEW(85L, DOCUMENT, "View document", new AuditLogActionWithParams(AuditLogAction.DOCUMENT_VIEW, AuditLogType.DOCUMENT)),
    DOCUMENT_UPLOAD(32L, DOCUMENT, "Upload document", new AuditLogActionWithParams(AuditLogAction.DOCUMENT_UPLOAD, AuditLogType.DOCUMENT)),
    DOCUMENT_EDIT(87L, DOCUMENT, "Edit document", new AuditLogActionWithParams(AuditLogAction.DOCUMENT_EDIT, AuditLogType.DOCUMENT)),
    DOCUMENT_DELETE(29L, DOCUMENT, "Delete document", new AuditLogActionWithParams(AuditLogAction.DOCUMENT_DELETE, AuditLogType.DOCUMENT)),
    CCD_GENERATE_AND_VIEW(33L, DOCUMENT, "View CCD", new AuditLogActionWithParams(AuditLogAction.CCD_GENERATE_AND_VIEW, AuditLogType.DOCUMENT)),
    CCD_GENERATE_AND_DOWNLOAD(34L, DOCUMENT, "Download CCD", new AuditLogActionWithParams(AuditLogAction.CCD_GENERATE_AND_DOWNLOAD, AuditLogType.DOCUMENT)),
    FACESHEET_GENERATE_AND_DOWNLOAD(35L, DOCUMENT, "Download facesheet", new AuditLogActionWithParams(AuditLogAction.FACESHEET_GENERATE_AND_DOWNLOAD, AuditLogType.DOCUMENT)),
    FACESHEET_GENERATE_AND_VIEW(86L, DOCUMENT, "View facesheet", new AuditLogActionWithParams(AuditLogAction.FACESHEET_GENERATE_AND_VIEW, AuditLogType.DOCUMENT)),

    EVENT_NOTE_VIEW_LISTING(36L, EVENT_AND_NOTE, "View events&notes", new AuditLogActionWithParams(AuditLogAction.EVENT_NOTE_VIEW_LISTING, AuditLogType.EVENT)),
    EVENT_VIEW(37L, EVENT_AND_NOTE, "View event details", new AuditLogActionWithParams(AuditLogAction.EVENT_VIEW, AuditLogType.EVENT)),
    EVENT_CREATE(38L, EVENT_AND_NOTE, "Create event", new AuditLogActionWithParams(AuditLogAction.EVENT_CREATE, AuditLogType.EVENT)),
    NOTE_VIEW(39L, EVENT_AND_NOTE, "View note details", new AuditLogActionWithParams(AuditLogAction.NOTE_VIEW, AuditLogType.NOTE)),
    NOTE_CREATE(40L, EVENT_AND_NOTE, "Create note", new AuditLogActionWithParams(AuditLogAction.NOTE_CREATE, AuditLogActionGroup.NOTE, List
            .of(NoteType.EVENT_NOTE.name(), NoteType.PATIENT_NOTE.name()), AuditLogType.NOTE)),
    NOTE_GROUP_CREATE(41L, EVENT_AND_NOTE, "Create group note", new AuditLogActionWithParams(AuditLogAction.NOTE_CREATE, AuditLogActionGroup.NOTE, List
            .of(NoteType.GROUP_NOTE.name()), AuditLogType.NOTE)),
    NOTE_EDIT(42L, EVENT_AND_NOTE, "Edit note", new AuditLogActionWithParams(AuditLogAction.NOTE_EDIT, AuditLogActionGroup.NOTE, List
            .of(NoteType.EVENT_NOTE.name(), NoteType.PATIENT_NOTE.name()), AuditLogType.NOTE)),
    NOTE_GROUP_EDIT(43L, EVENT_AND_NOTE, "Edit group note", new AuditLogActionWithParams(AuditLogAction.NOTE_EDIT, AuditLogActionGroup.NOTE, List.of(NoteType.GROUP_NOTE.name()), AuditLogType.NOTE)),

    CLIENT_CARE_TEAM_VIEW(44L, CLIENT_CARE_TEAM, "View care team", new AuditLogActionWithParams(AuditLogAction.CLIENT_CARE_TEAM_VIEW, AuditLogType.CLIENT_CTM)),
    CLIENT_CARE_TEAM_CREATE(45L, CLIENT_CARE_TEAM, "Add care team member", new AuditLogActionWithParams(AuditLogAction.CLIENT_CARE_TEAM_CREATE, AuditLogType.CLIENT_CTM)),
    CLIENT_CARE_TEAM_DELETE(46L, CLIENT_CARE_TEAM, "Delete care team member", new AuditLogActionWithParams(AuditLogAction.CLIENT_CARE_TEAM_DELETE, AuditLogType.CLIENT_CTM)),
    CLIENT_CARE_TEAM_EDIT(47L, CLIENT_CARE_TEAM, "Update notification settings", new AuditLogActionWithParams(AuditLogAction.CLIENT_CARE_TEAM_EDIT, AuditLogType.CLIENT_CTM)),

    INCIDENT_REPORT_VIEW_LISTING(48L, INCIDENT_REPORT, "View incident reports", new AuditLogActionWithParams(AuditLogAction.INCIDENT_REPORT_VIEW_LISTING, AuditLogType.INCIDENT_REPORT)),
    INCIDENT_REPORT_VIEW(49L, INCIDENT_REPORT, "View incident report", new AuditLogActionWithParams(AuditLogAction.INCIDENT_REPORT_VIEW, AuditLogType.INCIDENT_REPORT)),
    INCIDENT_REPORT_DOWNLOAD(50L, INCIDENT_REPORT, "Download incident report", new AuditLogActionWithParams(AuditLogAction.INCIDENT_REPORT_DOWNLOAD, AuditLogType.INCIDENT_REPORT)),
    INCIDENT_REPORT_EDIT(51L, INCIDENT_REPORT, "Edit incident report", new AuditLogActionWithParams(AuditLogAction.INCIDENT_REPORT_EDIT, AuditLogType.INCIDENT_REPORT)),
    INCIDENT_REPORT_CREATE(52L, INCIDENT_REPORT, "Create incident report", new AuditLogActionWithParams(AuditLogAction.INCIDENT_REPORT_CREATE, AuditLogType.INCIDENT_REPORT)),

    LAB_ORDER_VIEW_LISTING(53L, LAB, "View lab orders", new AuditLogActionWithParams(AuditLogAction.LAB_ORDER_VIEW_LISTING, AuditLogType.LAB_RESEARCH_ORDER)),
    LAB_ORDER_VIEW(54L, LAB, "View order", new AuditLogActionWithParams(AuditLogAction.LAB_ORDER_VIEW, AuditLogType.LAB_RESEARCH_ORDER)),
    LAB_ORDER_PLACE(55L, LAB, "Place order", new AuditLogActionWithParams(AuditLogAction.LAB_ORDER_CREATE, AuditLogType.LAB_RESEARCH_ORDER)),
    LAB_ORDER_REVIEW(56L, LAB, "Review order", new AuditLogActionWithParams(AuditLogAction.LAB_ORDER_REVIEW, AuditLogType.LAB_RESEARCH_ORDER)),

    CONTACT_VIEW_LISTING(57L, CONTACT, "View contacts", new AuditLogActionWithParams(AuditLogAction.CONTACT_VIEW_LISTING, AuditLogType.CONTACT)),
    CONTACT_VIEW(58L, CONTACT, "View contact", new AuditLogActionWithParams(AuditLogAction.CONTACT_VIEW, AuditLogType.CONTACT)),
    CONTACT_EDIT(59L, CONTACT, "Edit contact", new AuditLogActionWithParams(AuditLogAction.CONTACT_EDIT, AuditLogType.CONTACT)),
    CONTACT_CREATE(60L, CONTACT, "Create contact", new AuditLogActionWithParams(AuditLogAction.CONTACT_CREATE, AuditLogType.CONTACT)),
    CONTACT_INACTIVE(61L, CONTACT, "Mark contact as inactive", new AuditLogActionWithParams(AuditLogAction.CONTACT_INACTIVATE, AuditLogType.CONTACT)),
    CONTACT_REINVITE(62L, CONTACT, "Re-invite contact", new AuditLogActionWithParams(AuditLogAction.CONTACT_REINVITE, AuditLogType.CONTACT)),
    CONTACT_INVITE_ACCEPTED(63L, CONTACT, "Accept invite", new AuditLogActionWithParams(AuditLogAction.CONTACT_INVITE_ACCEPTED, AuditLogType.CONTACT)),

    ORGANIZATION_VIEW_LISTING(64L, ORGANIZATION, "View organizations", new AuditLogActionWithParams(AuditLogAction.ORGANIZATION_VIEW_LISTING, AuditLogType.ORGANIZATION)),
    ORGANIZATION_VIEW(65L, ORGANIZATION, "View organization details", new AuditLogActionWithParams(AuditLogAction.ORGANIZATION_VIEW, AuditLogType.ORGANIZATION)),
    ORGANIZATION_EDIT(66L, ORGANIZATION, "Edit organization", new AuditLogActionWithParams(AuditLogAction.ORGANIZATION_EDIT, AuditLogType.ORGANIZATION)),
    ORGANIZATION_CREATE(92L, ORGANIZATION, "Create organization", new AuditLogActionWithParams(AuditLogAction.ORGANIZATION_CREATE, AuditLogType.ORGANIZATION)),

    COMMUNITY_VIEW_LISTING(67L, COMMUNITY, "View communities", new AuditLogActionWithParams(AuditLogAction.COMMUNITY_VIEW_LISTING, AuditLogType.COMMUNITY)),
    COMMUNITY_VIEW(68L, COMMUNITY, "View community details", new AuditLogActionWithParams(AuditLogAction.COMMUNITY_VIEW, AuditLogType.COMMUNITY)),
    COMMUNITY_EDIT(69L, COMMUNITY, "Edit community", new AuditLogActionWithParams(AuditLogAction.COMMUNITY_EDIT, AuditLogType.COMMUNITY)),
    COMMUNITY_CREATE(93L, COMMUNITY, "Create community", new AuditLogActionWithParams(AuditLogAction.COMMUNITY_CREATE, AuditLogType.COMMUNITY)),

    COMMUNITY_CARE_TEAM_MEMBER_CREATE(70L, COMMUNITY_CARE_TEAM, "Add care team member", new AuditLogActionWithParams(AuditLogAction.COMMUNITY_CARE_TEAM_MEMBER_CREATE, AuditLogType.COMMUNITY_CTM)),
    COMMUNITY_CARE_TEAM_MEMBER_DELETE(71L, COMMUNITY_CARE_TEAM, "Delete care team member", new AuditLogActionWithParams(AuditLogAction.COMMUNITY_CARE_TEAM_MEMBER_DELETE, AuditLogType.COMMUNITY_CTM)),
    COMMUNITY_CARE_TEAM_MEMBER_EDIT(72L, COMMUNITY_CARE_TEAM, "Edit care team member", new AuditLogActionWithParams(AuditLogAction.COMMUNITY_CARE_TEAM_MEMBER_EDIT, AuditLogType.COMMUNITY_CTM)),

    REFERRAL_CREATE_FROM_MARKETPLACE(23L, REFERRAL, "Create referral from marketplace", new AuditLogActionWithParams(AuditLogAction.REFERRAL_CREATE_FROM_MARKETPLACE, AuditLogType.REFERRAL)),
    REFERRAL_INBOUND_VIEW_LISTING(73L, REFERRAL, "View inbound referrals", new AuditLogActionWithParams(AuditLogAction.REFERRAL_INBOUND_VIEW_LISTING, AuditLogType.REFERRAL)),
    REFERRAL_OUTBOUND_VIEW_LISTING(74L, REFERRAL, "View outbound referrals", new AuditLogActionWithParams(AuditLogAction.REFERRAL_OUTBOUND_VIEW_LISTING, AuditLogType.REFERRAL)),
    REFERRAL_INBOUND_VIEW(75L, REFERRAL, "View details of inbound referral", new AuditLogActionWithParams(AuditLogAction.REFERRAL_INBOUND_VIEW, AuditLogType.REFERRAL)),
    REFERRAL_OUTBOUND_VIEW(76L, REFERRAL, "View details of outbound referral", new AuditLogActionWithParams(AuditLogAction.REFERRAL_OUTBOUND_VIEW, AuditLogType.REFERRAL)),
    REFERRAL_CREATE(77L, REFERRAL, "Create referral", new AuditLogActionWithParams(AuditLogAction.REFERRAL_CREATE, AuditLogType.REFERRAL)),
    REFERRAL_REQUEST_CANCEL(78L, REFERRAL, "Cancel referral request", new AuditLogActionWithParams(AuditLogAction.REFERRAL_REQUEST_CANCEL, AuditLogType.REFERRAL_REQUEST)),
    REFERRAL_REQUEST_DECLINE(79L, REFERRAL, "Decline referral request", new AuditLogActionWithParams(AuditLogAction.REFERRAL_REQUEST_DECLINE, AuditLogType.REFERRAL_REQUEST)),
    REFERRAL_REQUEST_ACCEPT(80L, REFERRAL, "Accept referral request", new AuditLogActionWithParams(AuditLogAction.REFERRAL_REQUEST_ACCEPT, AuditLogType.REFERRAL_REQUEST)),
    REFERRAL_REQUEST_PRE_ADMIT(81L, REFERRAL, "Pre-admit referral request", new AuditLogActionWithParams(AuditLogAction.REFERRAL_REQUEST_PRE_ADMIT, AuditLogType.REFERRAL_REQUEST)),

    EXPENSE_VIEW_LISTING(28L, EXPENSE, "View list of expenses", new AuditLogActionWithParams(AuditLogAction.EXPENSE_VIEW_LISTING, AuditLogType.EXPENSE)),
    EXPENSE_VIEW(94L, EXPENSE, "View expense", new AuditLogActionWithParams(AuditLogAction.EXPENSE_VIEW, AuditLogType.EXPENSE)),
    EXPENSE_CREATE(95L, EXPENSE, "Create expense", new AuditLogActionWithParams(AuditLogAction.EXPENSE_CREATE, AuditLogType.EXPENSE)),

    COMPANY_DOCUMENT_VIEW_LISTING(27L, COMPANY_DOCUMENT, "View company documents", new AuditLogActionWithParams(AuditLogAction.COMPANY_DOCUMENT_VIEW_LISTING, AuditLogType.COMPANY_DOCUMENT)),
    COMPANY_DOCUMENT_UPLOAD(96L, COMPANY_DOCUMENT, "Upload document", new AuditLogActionWithParams(AuditLogAction.COMPANY_DOCUMENT_UPLOAD, AuditLogType.COMPANY_DOCUMENT)),
    COMPANY_DOCUMENT_DOWNLOAD(97L, COMPANY_DOCUMENT, "Download document(s)", new AuditLogActionWithParams(AuditLogAction.COMPANY_DOCUMENT_DOWNLOAD, AuditLogType.COMPANY_DOCUMENT)),
    CREATE_FOLDER(98L, COMPANY_DOCUMENT, "Create folder", new AuditLogActionWithParams(AuditLogAction.CREATE_FOLDER, AuditLogType.DOCUMENT_FOLDER)),
    EDIT_FOLDER(99L, COMPANY_DOCUMENT, "Edit folder", new AuditLogActionWithParams(AuditLogAction.EDIT_FOLDER, AuditLogType.DOCUMENT_FOLDER)),
    DELETE_FOLDER(100L, COMPANY_DOCUMENT, "Delete folder", new AuditLogActionWithParams(AuditLogAction.DELETE_FOLDER, AuditLogType.DOCUMENT_FOLDER)),

    MARKETPLACE_VIEW(23L, MARKETPLACE, "View Marketplace", new AuditLogActionWithParams(AuditLogAction.MARKETPLACE_VIEW, AuditLogType.MARKETPLACE)),
    MARKETPLACE_VIEW_COMMUNITY_DETAILS(101L, MARKETPLACE, "View community details", new AuditLogActionWithParams(AuditLogAction.MARKETPLACE_VIEW_COMMUNITY_DETAILS, AuditLogType.MARKETPLACE)),
    MARKETPLACE_VIEW_PARTNER_PROVIDERS(102L, MARKETPLACE, "View partner providers", new AuditLogActionWithParams(AuditLogAction.MARKETPLACE_VIEW_PARTNER_PROVIDERS, AuditLogType.MARKETPLACE)),

    ESIGN_BUILDER_TEMPLATE_VIEW_LISTING(26L, ESIGN_BUILDER, "View templates", new AuditLogActionWithParams(AuditLogAction.ESIGN_BUILDER_TEMPLATE_VIEW_LISTING, AuditLogType.SIGNATURE_TEMPLATE)),
    ESIGN_BUILDER_TEMPLATE_CREATE(103L, ESIGN_BUILDER, "Create a new template", new AuditLogActionWithParams(AuditLogAction.ESIGN_BUILDER_TEMPLATE_CREATE, AuditLogType.SIGNATURE_TEMPLATE)),
    ESIGN_BUILDER_TEMPLATE_UPDATE(104L, ESIGN_BUILDER, "Update template", new AuditLogActionWithParams(AuditLogAction.ESIGN_BUILDER_TEMPLATE_EDIT, AuditLogType.SIGNATURE_TEMPLATE)),
    ESIGN_BUILDER_TEMPLATE_DELETE(105L, ESIGN_BUILDER, "Delete template", new AuditLogActionWithParams(AuditLogAction.ESIGN_BUILDER_TEMPLATE_DELETE, AuditLogType.SIGNATURE_TEMPLATE)),

    APPOINTMENT_VIEW_LISTING(25L, APPOINTMENT, "View appointments", new AuditLogActionWithParams(AuditLogAction.APPOINTMENT_VIEW_LISTING, AuditLogType.APPOINTMENT)),
    APPOINTMENT_CREATE(106L, APPOINTMENT, "Create appointment", new AuditLogActionWithParams(AuditLogAction.APPOINTMENT_CREATE, AuditLogType.APPOINTMENT)),
    APPOINTMENT_UPDATE(107L, APPOINTMENT, "Update appointment", new AuditLogActionWithParams(AuditLogAction.APPOINTMENT_EDIT, AuditLogType.APPOINTMENT)),
    APPOINTMENT_CANCEL(108L, APPOINTMENT, "Cancel appointment", new AuditLogActionWithParams(AuditLogAction.APPOINTMENT_CANCEL, AuditLogType.APPOINTMENT)),
    APPOINTMENT_VIEW(109L, APPOINTMENT, "View appointment", new AuditLogActionWithParams(AuditLogAction.APPOINTMENT_VIEW, AuditLogType.APPOINTMENT)),
    APPOINTMENT_EXPORT(118L, APPOINTMENT, "Export appointments", new AuditLogActionWithParams(AuditLogAction.APPOINTMENT_EXPORT, AuditLogType.APPOINTMENT)),

    REPORT_EXPORT(110L, REPORT, "Export report", new AuditLogActionWithParams(AuditLogAction.REPORT_EXPORT, AuditLogType.REPORT)),

    CHAT_VIEW_LISTING(24L, CHAT_CALL, "View chats", new AuditLogActionWithParams(AuditLogAction.CHAT_VIEW_LISTING, AuditLogType.CHAT)),
    CHAT_CREATE(111L, CHAT_CALL, "Create chat", new AuditLogActionWithParams(AuditLogAction.CHAT_CREATE, AuditLogType.CHAT)),
    CHAT_PARTICIPANT_UPDATE(112L, CHAT_CALL, "Update chat participants", new AuditLogActionWithParams(AuditLogAction.CHAT_PARTICIPANT_UPDATE, AuditLogType.CHAT)),
    CALL_START(113L, CHAT_CALL, "Start call", new AuditLogActionWithParams(AuditLogAction.CALL_START, AuditLogType.CHAT)),

    SIGNATURE_REQUEST_SUBMIT(96L, SIGNATURE_REQUEST, "Send a signature request", new AuditLogActionWithParams(AuditLogAction.SIGNATURE_REQUEST_SUBMIT, AuditLogType.SIGNATURE_REQUEST)),
    SIGNATURE_REQUEST_CANCEL(97L, SIGNATURE_REQUEST, "Cancel signature request", new AuditLogActionWithParams(AuditLogAction.SIGNATURE_REQUEST_CANCEL, AuditLogType.SIGNATURE_REQUEST)),
    SIGNATURE_REQUEST_RESUBMIT(98L, SIGNATURE_REQUEST, "Resubmit expired signature request", new AuditLogActionWithParams(AuditLogAction.SIGNATURE_REQUEST_RESUBMIT, AuditLogType.SIGNATURE_REQUEST)),
    DOCUMENT_SIGN(99L, SIGNATURE_REQUEST, "Sign document", new AuditLogActionWithParams(AuditLogAction.DOCUMENT_SIGN, AuditLogType.SIGNATURE_REQUEST)),
    PIN_RESEND(100L, SIGNATURE_REQUEST, "Resend PIN", new AuditLogActionWithParams(AuditLogAction.PIN_RESEND, AuditLogType.SIGNATURE_REQUEST)),

    SIGNATURE_BULK_REQUEST_SUBMIT(101L, SIGNATURE_REQUEST, "Send a signature request", new AuditLogActionWithParams(AuditLogAction.SIGNATURE_BULK_REQUEST_SUBMIT, AuditLogType.SIGNATURE_BULK_REQUEST)),
    SIGNATURE_BULK_REQUEST_CANCEL(102L, SIGNATURE_REQUEST, "Cancel signature request", new AuditLogActionWithParams(AuditLogAction.SIGNATURE_BULK_REQUEST_CANCEL, AuditLogType.SIGNATURE_BULK_REQUEST)),
    SIGNATURE_BULK_REQUEST_RESUBMIT(103L, SIGNATURE_REQUEST, "Resubmit expired signature request", new AuditLogActionWithParams(AuditLogAction.SIGNATURE_BULK_REQUEST_RESUBMIT, AuditLogType.SIGNATURE_BULK_REQUEST)),

    RELEASE_NOTE_CREATE(104L, HELP, "Upload release note", new AuditLogActionWithParams(AuditLogAction.RELEASE_NOTE_UPLOAD, AuditLogType.RELEASE_NOTE)),
    RELEASE_NOTE_DOWNLOAD(105L, HELP, "Download release note", new AuditLogActionWithParams(AuditLogAction.RELEASE_NOTE_DOWNLOAD, AuditLogType.RELEASE_NOTE)),
    RELEASE_NOTE_VIEW_LISTING(106L, HELP, "View release notes", new AuditLogActionWithParams(AuditLogAction.RELEASE_NOTE_VIEW_LISTING, AuditLogType.RELEASE_NOTE)),
    USER_MANUAL_CREATE(107L, HELP, "Download user manual", new AuditLogActionWithParams(AuditLogAction.USER_MANUAL_UPLOAD, AuditLogType.USER_MANUAL)),
    USER_MANUAL_DOWNLOAD(108L, HELP, "Download user manual", new AuditLogActionWithParams(AuditLogAction.USER_MANUAL_DOWNLOAD, AuditLogType.USER_MANUAL)),
    USER_MANUAL_VIEW_LISTING(109L, HELP, "View user manuals", new AuditLogActionWithParams(AuditLogAction.USER_MANUAL_VIEW_LISTING, AuditLogType.USER_MANUAL)),
    SUPPORT_TICKET_CREATE(110L, HELP, "Submit contact us request", new AuditLogActionWithParams(AuditLogAction.SUPPORT_TICKET_CREATE, AuditLogType.SUPPORT_TICKET)),

    RECORD_SEARCH(22L, AuditLogActivityGroup.RECORD_SEARCH, "Record search", new AuditLogActionWithParams(AuditLogAction.RECORD_SEARCH, AuditLogType.RECORD_SEARCH)),

    PROSPECT_CREATE(119L, PROSPECT, "Create prospect record", new AuditLogActionWithParams(AuditLogAction.PROSPECT_CREATE, AuditLogType.PROSPECT)),
    PROSPECT_UPDATE(120L, PROSPECT, "Update prospect record", new AuditLogActionWithParams(AuditLogAction.PROSPECT_UPDATE, AuditLogType.PROSPECT)),
    PROSPECT_DEACTIVATE(121L, PROSPECT, "Deactivate prospect record", new AuditLogActionWithParams(AuditLogAction.PROSPECT_DEACTIVATE, AuditLogType.PROSPECT)),
    PROSPECT_ACTIVATE(122L, PROSPECT, "Activate prospect record", new AuditLogActionWithParams(AuditLogAction.PROSPECT_ACTIVATE, AuditLogType.PROSPECT)),
    PROSPECT_VIEW_LISTING(123L, PROSPECT, "View prospect listing", new AuditLogActionWithParams(AuditLogAction.PROSPECT_VIEW_LISTING, AuditLogType.PROSPECT)),
    PROSPECT_VIEW(124L, PROSPECT, "View prospect profile", new AuditLogActionWithParams(AuditLogAction.PROSPECT_VIEW, AuditLogType.PROSPECT));

    private final Long id;
    private final AuditLogActivityGroup group;
    private final String displayName;
    private final AuditLogActionWithParams actionWithParams;

    AuditLogActivity(Long id, AuditLogActivityGroup group, String displayName, AuditLogActionWithParams actionWithParams) {
        this.id = id;
        this.group = group;
        this.displayName = displayName;
        this.actionWithParams = actionWithParams;
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public AuditLogActivityGroup getGroup() {
        return group;
    }

    public AuditLogActionWithParams getActionWithParams() {
        return actionWithParams;
    }

    public static List<AuditLogActionWithParams> getAuditLogActionWithParamsByIds(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return Arrays.stream(AuditLogActivity.values())
                .filter(a -> ids.contains(a.id))
                .map(a -> a.actionWithParams)
                .collect(Collectors.toList());
    }

    public static AuditLogActivity getByAuditLogAction(AuditLogAction action) {
        return Arrays.stream(AuditLogActivity.values()).filter(a -> action == a.actionWithParams.getAction()).findFirst().orElse(null);
    }
}

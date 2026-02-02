package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.entity.note.NoteType;
import com.scnsoft.eldermark.entity.referral.ReferralRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class UrlServiceImpl implements UrlService {

    @Value("${portal.url}")
    private String portalUrl;

    @Override
    public String eventUrl(Event event) {
        return getBaseClientEventNoteUrl(event.getClient()) + "?eventId=" + event.getId();
    }

    @Override
    public String noteUrl(Note note) {
        if (NoteType.GROUP_NOTE.equals(note.getType())) {
            return portalUrl + "events?noteId=" + note.getId() + "&organizationId=" + note.getNoteClients().get(0).getOrganizationId();
        }
        return getBaseClientEventNoteUrl(note.getNoteClients().get(0)) + "?noteId=" + note.getId();
    }

    @Override
    public String conversationsUrl() {
        return portalUrl + "chats";
    }

    @Override
    public String conversationUrl(String conversationSid) {
        return conversationsUrl() + "?conversationSid=" + conversationSid;
    }

    @Override
    public String referralRequestInboundUrl(ReferralRequest referralRequest) {
        return portalUrl + "inbound-referrals/" + referralRequest.getReferral().getId() + "/requests/" + referralRequest.getId();
    }

    @Override
    public String referralRequestInboundExternalUrl(ReferralRequest referralRequest) {
        return portalUrl + "external-provider/inbound-referrals/" + referralRequest.getReferral().getId() + "/requests/" + referralRequest.getId();
    }

    @Override
    public String referralRequestInboundListUrl() {
        return portalUrl + "inbound-referrals";
    }

    @Override
    public String referralRequestOutboundUrl(ReferralRequest referralRequest) {
        return portalUrl + "outbound-referrals/" + referralRequest.getReferral().getId();
    }

    @Override
    public String labResearchOrderUrl(LabResearchOrder labResearchOrder) {
        return portalUrl + "labs/" + labResearchOrder.getId();
    }

    @Override
    public String labResearchOrderBulkReviewUrl(Long organizationId) {
        return portalUrl + "labs?organizationId=" + organizationId;
    }

    @Override
    public String incidentReportDetailsUrl(IncidentReport incidentReport) {
        return portalUrl + "incident-reports/" + incidentReport.getId();
    }

    @Override
    public String clientDashboardUrl(Client client) {
        return portalUrl + "clients/" + client.getId();
    }

    @Override
    public String signatureRequestUrl(Long clientId, Long requestId) {
        return portalUrl + "signature-notification?clientId=" + clientId + "&requestId=" + requestId;
    }

    @Override
    public String createReferralRequestExternalUrl(Long organizationId, Long communityId, Long providerCommunityId) {
        return String.format(
                "%smarketplace?organizationId=%d&communityId=%d&providerId=%d&shouldCreateReferral=true",
                portalUrl,
                organizationId,
                communityId,
                providerCommunityId
        );
    }

    @Override
    public String appointmentUrl(ClientAppointment appointment) {

        var chainId = appointment.getChainId() != null
                ? appointment.getChainId()
                : appointment.getId();

        return portalUrl + "appointments?appointmentChainId=" + chainId;
    }

    private String getBaseClientEventNoteUrl(Client client) {
        return portalUrl + "clients/" + client.getId() + "/events";
    }

    @Override
    public String referralRequestExternalUrl(ReferralRequest referralRequest, String token) {
        return portalUrl + "external-provider/inbound-referrals/" + referralRequest.getReferral().getId() + "/requests/" + referralRequest.getId() + (StringUtils.isNotEmpty(token) ? "?token=" + token : "");    }

    @Override
    public String careTeamInvitationUrl(Long careTeamInvitationId) {
        return portalUrl + "care-team-invitations?id=" + careTeamInvitationId;
    }
}

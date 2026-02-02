package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.entity.referral.ReferralRequest;

public interface UrlService {

    String eventUrl(Event event);

    String noteUrl(Note note);

    String conversationsUrl();

    String conversationUrl(String conversationSid);

    String referralRequestInboundUrl(ReferralRequest referralRequest);

    String referralRequestInboundExternalUrl(ReferralRequest referralRequest);

    String referralRequestInboundListUrl();

    String referralRequestOutboundUrl(ReferralRequest referralRequest);

    String labResearchOrderUrl(LabResearchOrder labResearchOrder);

    String labResearchOrderBulkReviewUrl(Long organizationId);

    String incidentReportDetailsUrl(IncidentReport incidentReport);

    String referralRequestExternalUrl(ReferralRequest referralRequest, String token);

    String clientDashboardUrl(Client client);

    String signatureRequestUrl(Long clientId, Long requestId);

    String createReferralRequestExternalUrl(Long organizationId, Long communityId, Long providerCommunityId);

    String appointmentUrl(ClientAppointment appointment);

    String careTeamInvitationUrl(Long careTeamInvitationId);
}

package com.scnsoft.eldermark.converter;

import com.scnsoft.eldermark.dto.notification.referral.ReferralRequestNotificationMailDto;
import com.scnsoft.eldermark.entity.ExternalEmployeeRequest;
import com.scnsoft.eldermark.entity.basic.DisplayableNamedCodedEntity;
import com.scnsoft.eldermark.entity.referral.ReferralRequestNotification;
import com.scnsoft.eldermark.service.ExternalEmployeeInboundReferralCommunityService;
import com.scnsoft.eldermark.service.ExternalEmployeeRequestService;
import com.scnsoft.eldermark.service.UrlService;
import com.scnsoft.eldermark.util.ClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ReferralRequestNotificationMailDtoConverter {

    private static final String REFERRAL_REQUEST_BASE_SUBJECT_TEMPLATE = "The referral request for %s %s";
    private static final String REFERRAL_REQUEST_NEW_REQUEST_SUBJECT_TEMPLATE = "A new service request has been submitted";
    private static final String REFERRAL_REQUEST_INFO_REQUESTED_SUBJECT_TEMPLATE = "The request for information about the referral for";
    private static final String REFERRAL_REQUEST_INFO_REPLIED_SUBJECT_TEMPLATE = "Reply to a request for information about the referral for";


    @Autowired
    private UrlService urlService;

    @Autowired
    private ExternalEmployeeRequestService externalEmployeeRequestService;

    @Autowired
    private ExternalEmployeeInboundReferralCommunityService externalEmployeeInboundReferralCommunityService;

    public ReferralRequestNotificationMailDto convert(ReferralRequestNotification source) {
        ReferralRequestNotificationMailDto target = new ReferralRequestNotificationMailDto();
        if (source.getReferralRequest().getReferral().isMarketplace() && StringUtils.isEmpty(source.getEmployee().getFullName())) {
            target.setRecipientName("Service Provider");
        } else {
            target.setRecipientName(source.getEmployee().getFullName());
        }
        target.setEmail(source.getDestination());
        target.setType(source.getType());
        fillTypedDetails(source, target);
        return target;
    }

    private void fillTypedDetails(ReferralRequestNotification source, ReferralRequestNotificationMailDto target) {
        target.setClientName(ClientUtils.getInitials(source.getReferralRequest().getReferral().getClient(), " "));
        switch (source.getType()) {
            case NEW_REQUEST:
                target.setCategory(source.getReferralRequest().getReferral().getCategories().stream()
                        .map(DisplayableNamedCodedEntity::getDisplayName).collect(Collectors.joining(", ")));
                target.setService(source.getReferralRequest().getReferral().getServiceName());
                target.setPriority(source.getReferralRequest().getReferral().getPriority().getDisplayName());
                target.setCommunity(source.getReferralRequest().getReferral().getClient().getCommunity().getName());
                if (source.getReferralRequest().getReferral().isMarketplace()) {
                    var token = externalEmployeeRequestService.findByExternalEmployeeLoginNameAndCommunityId(source.getEmployee().getLoginName(), source.getReferralRequest().getCommunityId())
                            .map(ExternalEmployeeRequest::getToken)
                            .orElse(null);
                    target.setUrl(urlService.referralRequestExternalUrl(source.getReferralRequest(), token));
                } else if (source.isOrgAdmin()) {
                    target.setUrl(urlService.referralRequestInboundListUrl());
                } else {
                    target.setUrl(urlService.referralRequestInboundUrl(source.getReferralRequest()));
                }
                target.setSubject(REFERRAL_REQUEST_NEW_REQUEST_SUBJECT_TEMPLATE);
                target.setTemplateFile("referral/ReferralNotificationNewRequest.vm");
                break;
            case PRE_ADMITTED:
                target.setTypePhrase("has been pre-admitted");
                target.setSubject(String.format(REFERRAL_REQUEST_BASE_SUBJECT_TEMPLATE, target.getClientName(), target.getTypePhrase()));
                target.setUrl(urlService.referralRequestOutboundUrl(source.getReferralRequest()));
                target.setTemplateFile("referral/ReferralNotificationBase.vm");
                break;
            case NOT_AVAILABLE:
                target.setTypePhrase("is no longer available");
                target.setSubject(String.format(REFERRAL_REQUEST_BASE_SUBJECT_TEMPLATE, target.getClientName(), target.getTypePhrase()));
                target.setTemplateFile("referral/ReferralNotificationBase.vm");
                break;
            case ACCEPTED:
                target.setTypePhrase("has been accepted");
                target.setSubject(String.format(REFERRAL_REQUEST_BASE_SUBJECT_TEMPLATE, target.getClientName(), target.getTypePhrase()));
                target.setUrl(urlService.referralRequestOutboundUrl(source.getReferralRequest()));
                target.setTemplateFile("referral/ReferralNotificationBase.vm");
                break;
            case DECLINED:
                target.setTypePhrase("has been declined");
                target.setCommunity(source.getReferralRequest().getCommunity().getName());
                target.setSubject(String.format(REFERRAL_REQUEST_BASE_SUBJECT_TEMPLATE, target.getClientName(), target.getTypePhrase()));
                target.setUrl(urlService.referralRequestOutboundUrl(source.getReferralRequest()));
                target.setTemplateFile("referral/ReferralNotificationDeclined.vm");
                break;
            case INFO_REQUESTED:
                target.setSubject(REFERRAL_REQUEST_INFO_REQUESTED_SUBJECT_TEMPLATE + " " + target.getClientName());
                target.setUrl(urlService.referralRequestOutboundUrl(source.getReferralRequest()));
                target.setTemplateFile("referral/ReferralNotificationInfo.vm");
                break;
            case INFO_REPLIED:
                target.setSubject(REFERRAL_REQUEST_INFO_REPLIED_SUBJECT_TEMPLATE + " " + target.getClientName());
                var url = externalEmployeeInboundReferralCommunityService.isExternalEmployee(source.getEmployee())
                        ? urlService.referralRequestInboundExternalUrl(source.getReferralRequest())
                        : urlService.referralRequestInboundUrl(source.getReferralRequest());
                target.setUrl(url);
                target.setTemplateFile("referral/ReferralNotificationInfoReply.vm");
                break;
            case CANCELED:
                target.setTypePhrase("has been cancelled by the referral requester");
                target.setSubject(String.format(REFERRAL_REQUEST_BASE_SUBJECT_TEMPLATE, target.getClientName(), "has been cancelled"));
                target.setTemplateFile("referral/ReferralNotificationBase.vm");
                break;
            case ASSIGN:
                target.setTypePhrase("has been assigned to you");
                target.setSubject(String.format(REFERRAL_REQUEST_BASE_SUBJECT_TEMPLATE, target.getClientName(), target.getTypePhrase()));
                target.setUrl(urlService.referralRequestInboundUrl(source.getReferralRequest()));
                target.setTemplateFile("referral/ReferralNotificationAssign.vm");
                break;
        }
    }
}

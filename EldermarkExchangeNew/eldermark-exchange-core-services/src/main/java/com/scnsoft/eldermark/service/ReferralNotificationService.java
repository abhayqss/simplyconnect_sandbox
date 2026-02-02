package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.referral.*;

import java.util.List;

public interface ReferralNotificationService {

    void sendSubmitNotifications(Referral referral);

    void sendPreAdmitNotification(ReferralRequest referralRequest);

    void sendAcceptedNotification(ReferralRequest referralRequest);

    void sendDeclinedNotification(ReferralRequest referralRequest, ReferralStatus referralStatus);

    void sendCanceledNotification(Referral referral);

    void sendInfoReqNotification(ReferralInfoRequest referralInfoRequest);

    void sendReplyInfoReqNotification(ReferralInfoRequest referralInfoRequest);

    void sendAssignedToYouNotification(ReferralRequest referralRequest);


}

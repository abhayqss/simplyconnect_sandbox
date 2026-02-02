package com.scnsoft.eldermark.beans.twilio.user;

import com.scnsoft.eldermark.beans.projection.AssociatedClientIdsAware;
import com.scnsoft.eldermark.beans.projection.AvatarIdNameAware;
import com.scnsoft.eldermark.beans.projection.CareTeamRoleIdAware;
import com.scnsoft.eldermark.entity.client.ClientNameAndCommunityAware;

public interface EmployeeTwilioCommunicationsUser extends ClientNameAndCommunityAware, AssociatedClientIdsAware, CareTeamRoleIdAware, EmployeeTwilioSecurityFieldsAware, AvatarIdNameAware {

    String getTwilioUserSid();

    void setTwilioUserSid(String twilioUserSid);

    String getTwilioServiceConversationSid();

    void setTwilioServiceConversationSid(String twilioUserSid);
}

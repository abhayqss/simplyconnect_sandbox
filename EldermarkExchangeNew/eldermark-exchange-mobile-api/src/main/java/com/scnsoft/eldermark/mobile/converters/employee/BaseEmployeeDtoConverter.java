package com.scnsoft.eldermark.mobile.converters.employee;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.IdNamesBirthDateAware;
import com.scnsoft.eldermark.mobile.dto.employee.EmployeeDto;
import com.scnsoft.eldermark.service.security.*;
import com.scnsoft.eldermark.service.twilio.ChatService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Objects;

public abstract class BaseEmployeeDtoConverter {
    @Autowired
    private ChatService chatService;

    @Autowired
    private VideoCallSecurityService videoCallSecurityService;

    @Autowired
    protected LoggedUserService loggedUserService;

    @Autowired
    protected PermissionFilterService permissionFilterService;

    @Autowired
    private EmployeeMobileSecurityService employeeSecurityService;

    void fillIdNamesBirthDate(IdNamesBirthDateAware source, EmployeeDto target) {
        target.setId(source.getId());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setBirthDate(source.getBirthDate());
        target.setCanEdit(employeeSecurityService.canEdit(source.getId()));
    }

    void fillConversationsData(Long sourceId, String sourceTwilioUserSid, EmployeeDto target) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        fillConversationsData(sourceId, sourceTwilioUserSid, target, permissionFilter);
    }

    void fillConversationsData(Long sourceId, String sourceTwilioUserSid, EmployeeDto target, PermissionFilter permissionFilter) {
        target.setTwilioUserSid(sourceTwilioUserSid);
        var currentEmployeeId = loggedUserService.getCurrentEmployeeId();
        if (!Objects.equals(currentEmployeeId, sourceId)) {
            chatService.findPersonalChatSid(currentEmployeeId, sourceId)
                    .ifPresent(target::setConversationSid);
            target.setCanStartConversation(StringUtils.isEmpty(target.getConversationSid()));
        } else {
            target.setCanStartConversation(false);
        }
        target.setCanCall(videoCallSecurityService.canStart(null, Collections.singletonList(sourceId), permissionFilter));
    }
}

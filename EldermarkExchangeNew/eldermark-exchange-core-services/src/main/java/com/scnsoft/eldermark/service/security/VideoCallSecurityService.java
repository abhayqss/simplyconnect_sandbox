package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.twilio.user.EmployeeTwilioSecurityFieldsAware;
import com.scnsoft.eldermark.entity.Employee;

import java.util.Collection;

public interface VideoCallSecurityService {

    boolean canStart(String conversationSid, Collection<Long> employeeIds);

    boolean canStart(String conversationSid, Collection<Long> employeeIds, PermissionFilter filter);

    boolean canAddMembers(Collection<Long> employeeIds);

    boolean canViewHistory();

    boolean canStartIrCall(String conversationSid, Long incidentReportId);

    boolean areVideoCallsAccessibleByEmployee(Employee employee);
}

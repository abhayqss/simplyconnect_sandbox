package com.scnsoft.eldermark.service.twilio;

import com.scnsoft.eldermark.beans.twilio.user.EmployeeTwilioSecurityFieldsAware;
import com.scnsoft.eldermark.beans.twilio.video.AddParticipantOutcomeItem;
import com.scnsoft.eldermark.beans.twilio.video.InitiateCallOutcome;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;

import java.util.List;
import java.util.Set;

public interface VideoCallService {

    boolean isVideoCallEnabled(Long employeeId);

    boolean isVideoCallEnabled(EmployeeTwilioSecurityFieldsAware employee);

    boolean isVideoCallEnabled(Employee employee);

    boolean isVideoCallEnabled(String conversationSid, Long selfId);

    InitiateCallOutcome initiateCallInConversation(String conversationSid, String friendlyName, Set<Long> employeeIds, Long callerEmployeeId);

    InitiateCallOutcome initiateCallForEmployees(String friendlyName, Set<Long> employeeIds, Long callerEmployeeId, Long participatingClientId);

    InitiateCallOutcome initiateCallInIrConversation(Long incidentReportId, Long callerEmployeeId);

    List<AddParticipantOutcomeItem> addParticipants(String roomSid, String friendlyName, Set<Long> addEmployeeIds, Long actorEmployeeId, Long participatingClientId);

    void declineCall(String roomSid, Long calleeEmployeeId);

    void declineCall(String roomSid, Long calleeEmployeeId, String devicePushNotificationToken);

    void removeParticipants(String roomSid, Set<String> identities, Long actorEmployeeId);

    Pair<String, String> getActiveCallRoomWithToken(Long currentEmployeeId, String conversationSid);

    List<AddParticipantOutcomeItem> addNonActiveOrPendingCallConversationParticipants(String roomSid, Set<Long> nonActiveOrPendingCallEmployeeIds, Long actorEmployeeId);

    void muteParticipant(String roomSid, Long employeeId, Long actorEmployeeId);

    boolean isCallActiveAndEmployeeOnCallOrHasIncomingCall(String roomSid, Long employeeId);
}

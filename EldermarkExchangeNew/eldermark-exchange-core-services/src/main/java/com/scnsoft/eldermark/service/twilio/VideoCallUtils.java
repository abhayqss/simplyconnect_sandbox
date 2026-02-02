package com.scnsoft.eldermark.service.twilio;

import com.scnsoft.eldermark.entity.video.VideoCallHistory;
import com.scnsoft.eldermark.entity.video.VideoCallParticipantHistory;
import com.scnsoft.eldermark.entity.video.VideoCallParticipantState;
import com.scnsoft.eldermark.entity.video.VideoCallParticipantStateEndReason;
import com.scnsoft.eldermark.exception.BusinessException;
import com.twilio.jwt.accesstoken.VideoGrant;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class VideoCallUtils {
    public static final Set<VideoCallParticipantState> INCOMING_CALL_STATES = EnumSet.of(
            VideoCallParticipantState.INCOMING_CALL,
            VideoCallParticipantState.NEW_MEMBER_INCOMING_CALL);

    private VideoCallUtils() {
    }

    public static Stream<VideoCallParticipantHistory> filterWithActiveState(Collection<VideoCallParticipantHistory> participantHistories) {
        return participantHistories.stream()
                .filter(VideoCallUtils::isActiveEntry);
    }

    public static Set<String> getActiveIdentities(Collection<VideoCallParticipantHistory> participantHistories) {
        return getIdentities(filterWithActiveState(participantHistories));
    }

    public static Set<String> getIdentities(Collection<VideoCallParticipantHistory> participantHistories) {
        return getIdentities(participantHistories.stream());
    }

    public static Set<String> getIdentities(Stream<VideoCallParticipantHistory> participantHistories) {
        return participantHistories
                .map(VideoCallParticipantHistory::getTwilioIdentity).collect(Collectors.toSet());
    }

    public static VideoCallParticipantHistory findCallerOutgoingCallEntry(VideoCallHistory videoCallHistory) {
        return findFirstIdentityEntry(
                videoCallHistory.getParticipantsHistory().stream()
                        .filter(participantHistory -> participantHistory.getState().equals(VideoCallParticipantState.OUTGOING_CALL)),
                videoCallHistory.getCallerTwilioIdentity())
                .orElseThrow(() -> new BusinessException("Illegal state: Call without callee")); // shouldn't happen
    }

    public static Stream<VideoCallParticipantHistory> filterWithActiveIncomingCall(Collection<VideoCallParticipantHistory> participantHistories) {
        return filterWithActiveState(participantHistories)
                .filter(VideoCallUtils::isIncomingCall);
    }

    public static Stream<VideoCallParticipantHistory> filterWithIncomingCall(Collection<VideoCallParticipantHistory> participantHistories) {
        return participantHistories.stream()
                .filter(VideoCallUtils::isIncomingCall);
    }

    public static Stream<VideoCallParticipantHistory> filterWithActiveOnCall(Collection<VideoCallParticipantHistory> participantHistories) {
        return filterWithActiveState(participantHistories)
                .filter(VideoCallUtils::isOnCall);
    }

    public static Stream<VideoCallParticipantHistory> filterWithOnCall(Collection<VideoCallParticipantHistory> participantHistories) {
        return participantHistories.stream()
                .filter(VideoCallUtils::isOnCall);
    }

    public static boolean isActiveEntry(VideoCallParticipantHistory participantHistory) {
        return participantHistory.getStateEndReason() == null;
    }

    public static Optional<VideoCallParticipantHistory> findFirstIdentityEntry(Stream<VideoCallParticipantHistory> participantHistoryStream,
                                                                               String participantIdentity) {
        return participantHistoryStream
                .filter(participantHistory -> participantIdentity.equals(participantHistory.getTwilioIdentity()))
                .findFirst();
    }

    public static boolean isIncomingCall(VideoCallParticipantHistory participantHistory) {
        return INCOMING_CALL_STATES.contains(participantHistory.getState());
    }

    public static boolean isOnCall(VideoCallParticipantHistory participantHistory) {
        return VideoCallParticipantState.ON_CALL == participantHistory.getState();
    }

    public static boolean isOutgoingCall(VideoCallParticipantHistory participantHistory) {
        return VideoCallParticipantState.OUTGOING_CALL == participantHistory.getState();
    }

    public static boolean isActiveCall(VideoCallHistory callHistory) {
        return callHistory != null && callHistory.getEndDatetime() == null;
    }

    public static String getConversation(VideoCallHistory callHistory) {
        return Optional.ofNullable(callHistory.getUpdatedConversationSid()).orElse(callHistory.getInitialConversationSid());
    }

    public static Stream<VideoCallParticipantHistory> filterOfIdentity(Collection<VideoCallParticipantHistory> participantsHistory, String identity) {
        return participantsHistory.stream()
                .filter(entry -> entry.getTwilioIdentity().equals(identity));
    }

    public static Stream<VideoCallParticipantHistory> filterActiveInRoom(Collection<VideoCallParticipantHistory> participantsHistory) {
        return filterWithActiveState(participantsHistory)
                .filter(participantHistory -> VideoCallUtils.isOnCall(participantHistory) ||
                        VideoCallUtils.isOutgoingCall(participantHistory));

        //another approach is below, but it is going to work for local developer testing - twilioRoomSid is not populated
        //because it is frontend's action to enter the room.
//        return filterWithActiveState(participantsHistory)
//                .filter(participantHistory -> StringUtils.isNotEmpty(participantHistory.getTwilioRoomParticipantSid()));

    }

    public static Optional<VideoCallParticipantHistory> getLatestHistoryEntryForIdentity(Collection<VideoCallParticipantHistory> participantsHistory, String identity) {
        return filterOfIdentity(participantsHistory, identity)
                .reduce((entry1, entry2) -> {
                    if (entry1.getStateDatetime().isAfter(entry2.getStateDatetime())) {
                        return entry1;
                    } else {
                        return entry2;
                    }
                });
    }

    public static boolean participantWasRemoved(Collection<VideoCallParticipantHistory> participantHistories, String identity) {
        return getLatestHistoryEntryForIdentity(participantHistories, identity)
                .filter(latestEntryOfActor ->
                        latestEntryOfActor.getStateEndReason() == VideoCallParticipantStateEndReason.PARTICIPANT_REMOVED)
                .isPresent();
    }

    public static boolean isCallerInRoom(VideoCallHistory callHistory) {
        return filterOfIdentity(callHistory.getParticipantsHistory(), callHistory.getCallerTwilioIdentity())
                .map(VideoCallParticipantHistory::getTwilioRoomParticipantSid)
                .anyMatch(StringUtils::isNotEmpty);
    }

    public static boolean didCallStart(VideoCallHistory callHistory) {
        return filterWithOnCall(callHistory.getParticipantsHistory()).count() > 0;
    }

    public static String getRoomSidOrThrow(TwilioToken twilioToken) {
        return twilioToken.getVideoGrant().map(VideoGrant::getRoom).orElseThrow(
                () -> new IllegalArgumentException("Twilio access token should contain room sid")
        );
    }
}

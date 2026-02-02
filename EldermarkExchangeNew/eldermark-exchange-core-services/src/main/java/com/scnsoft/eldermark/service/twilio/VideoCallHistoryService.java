package com.scnsoft.eldermark.service.twilio;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.video.VideoCallHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VideoCallHistoryService {

    VideoCallHistory save(VideoCallHistory videoCallHistory);

    void writeCallEndTimeIfMissing(String roomSid, Instant when);

    VideoCallHistory findLockedByRoomSid(String roomSid);

    VideoCallHistory findLockedById(Long id);

    boolean isBusy(String identity);

    Page<VideoCallHistory> findByEmployeeId(PermissionFilter permissionFilter, Long employeeId, Pageable pageable);

    Optional<VideoCallHistory> findLockedActiveByConversationSid(String conversationSid);

    List<VideoCallHistory> findActiveCalls();

    void deleteCallHistoryForIdentities(Collection<String> identities);

    Set<String> getCallParticipantsIdentities(Long callHistoryId);

    void historyListViewed(Long employeeId);

    void createMissingReadStatuses(Long callHistoryId);
}

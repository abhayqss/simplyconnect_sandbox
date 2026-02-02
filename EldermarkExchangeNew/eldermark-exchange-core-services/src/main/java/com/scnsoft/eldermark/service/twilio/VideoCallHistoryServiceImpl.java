package com.scnsoft.eldermark.service.twilio;

import com.scnsoft.eldermark.beans.projection.TwilioIdentityAware;
import com.scnsoft.eldermark.beans.projection.VideoCallHistoryIdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.EmployeeMissedCallReadStatusDao;
import com.scnsoft.eldermark.dao.specification.SpecificationUtils;
import com.scnsoft.eldermark.dao.specification.VideoCallHistorySpecificationGenerator;
import com.scnsoft.eldermark.dao.video.VideoCallHistoryDao;
import com.scnsoft.eldermark.dao.video.VideoCallParticipantHistoryDao;
import com.scnsoft.eldermark.entity.video.VideoCallHistory;
import com.scnsoft.eldermark.entity.video.VideoCallHistory_;
import com.scnsoft.eldermark.entity.video.VideoCallParticipantHistory_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class VideoCallHistoryServiceImpl implements VideoCallHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(VideoCallHistoryServiceImpl.class);

    @Autowired
    private VideoCallHistoryDao videoCallHistoryDao;

    @Autowired
    private VideoCallParticipantHistoryDao videoCallParticipantHistoryDao;

    @Autowired
    private VideoCallHistorySpecificationGenerator videoCallHistorySpecificationGenerator;

    @Autowired
    private EmployeeMissedCallReadStatusDao employeeMissedCallReadStatusDao;

    @Override
    public VideoCallHistory save(VideoCallHistory videoCallHistory) {
        return videoCallHistoryDao.save(videoCallHistory);
    }

    @Override
    public void writeCallEndTimeIfMissing(String roomSid, Instant when) {
        videoCallHistoryDao.writeCallEndTimeIfMissing(roomSid, when);
    }

    @Override
    public VideoCallHistory findLockedByRoomSid(String roomSid) {
        return videoCallHistoryDao.findLockedByRoomSid(roomSid);
    }

    @Override
    public VideoCallHistory findLockedById(Long id) {
        return videoCallHistoryDao.findLockedById(id).orElseThrow();
    }

    @Override
    public boolean isBusy(String identity) {
        return videoCallParticipantHistoryDao.count((root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get(VideoCallParticipantHistory_.twilioIdentity), identity),
                criteriaBuilder.isNull(root.join(VideoCallParticipantHistory_.videoCallHistory).get(VideoCallHistory_.endDatetime)),
                criteriaBuilder.isNull(root.get(VideoCallParticipantHistory_.stateEndDatetime))
        )) > 0;
    }

    @Override
    public Page<VideoCallHistory> findByEmployeeId(PermissionFilter permissionFilter, Long employeeId, Pageable pageable) {

        var hasAccess = videoCallHistorySpecificationGenerator.hasAccess(permissionFilter);
        var withIdentity = videoCallHistorySpecificationGenerator.withIdentity(ConversationUtils.employeeIdToIdentity(employeeId));

        return videoCallHistoryDao.findAll(hasAccess.and(withIdentity), pageable);
    }

    @Override
    public Optional<VideoCallHistory> findLockedActiveByConversationSid(String conversationSid) {
        return videoCallHistoryDao.findLockedActiveByConversationSid(conversationSid);
    }

    @Override
    public List<VideoCallHistory> findActiveCalls() {
        return videoCallHistoryDao.findAllByEndDatetimeIsNull();
    }

    @Override
    public void deleteCallHistoryForIdentities(Collection<String> identities) {
        logger.info("Removing call history for identities {}", identities);
        var callHistoryIds = videoCallParticipantHistoryDao.findAll(
                        (root, criteriaQuery, criteriaBuilder) ->
                                SpecificationUtils.in(
                                        criteriaBuilder,
                                        root.get(VideoCallParticipantHistory_.twilioIdentity),
                                        identities),
                        VideoCallHistoryIdAware.class
                )
                .stream()
                .map(VideoCallHistoryIdAware::getVideoCallHistoryId)
                .collect(Collectors.toSet());

        logger.info("Found call history entries where identities {} participated: {}", identities, callHistoryIds);

        callHistoryIds.forEach(callHistoryId -> {
            videoCallHistoryDao.deleteById(callHistoryId);
            logger.info("Removed call history [{}]", callHistoryId);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getCallParticipantsIdentities(Long callHistoryId) {
        return videoCallParticipantHistoryDao.findAll(
                        videoCallHistorySpecificationGenerator.participantsOfCall(callHistoryId),
                        TwilioIdentityAware.class
                )
                .stream()
                .map(TwilioIdentityAware::getTwilioIdentity)
                .collect(Collectors.toSet());
    }


    @Override
    public void historyListViewed(Long employeeId) {
        employeeMissedCallReadStatusDao.updateLastHistoryReadForEmployee(employeeId, Instant.now());
    }

    @Override
    public void createMissingReadStatuses(Long callHistoryId) {
        employeeMissedCallReadStatusDao.createMissingReadStatuses(callHistoryId, Instant.now());
    }
}

package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.ActivityDao;
import com.scnsoft.eldermark.dao.phr.EventReadStatusDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.*;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.web.entity.*;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author phomal
 * Created on 5/12/2017
 */
@Service
@Transactional
public class ActivityService {

    private final ActivityDao activityDao;

    private static EventReadStatusDao eventReadStatusDao;

    @Autowired
    public ActivityService(ActivityDao activityDao, EventReadStatusDao eventReadStatusDao) {
        this.activityDao = activityDao;
        ActivityService.eventReadStatusDao = eventReadStatusDao;
    }

    InvitationActivity logInvitationActivity(Long patientUserId, Employee invitee, InvitationActivity.Status status) {
        InvitationActivity activity = new InvitationActivity();
        activity.setEmployee(invitee);
        activity.setPatientId(patientUserId);
        activity.setStatus(status);
        activity.setDate(new Date());
        return activityDao.save(activity);
    }

    static List<ActivityDto> transform(List<Activity> activities) {
        List<ActivityDto> dtos = new ArrayList<>();
        for (Activity activity : activities) {
            ActivityDto dto = null;
            if (activity instanceof InvitationActivity) {
                dto = transform((InvitationActivity) activity);
            } else if (activity instanceof CallActivity) {
                dto = transform((CallActivity) activity);
            } else if (activity instanceof VideoActivity) {
                dto = transform((VideoActivity) activity);
            } else if (activity instanceof EventActivity) {
                dto = transform((EventActivity) activity);
            }
            if (dto == null) {
                continue;
            }
            dto.setDate(activity.getDate().getTime());
            dtos.add(dto);
        }
        return dtos;
    }

    private static EventActivityDto transform(EventActivity activity) {
        EventActivityDto dto = new EventActivityDto();
        dto.setType(ActivityDto.Type.EVENT);
        dto.setEventId(activity.getEventId());
        dto.setEventType(activity.getEventType().getDescription());
        dto.setResponsibility(activity.getResponsibility().getDescription());

        Long currentUserId = PhrSecurityUtils.getCurrentUserId();
        boolean isRead = eventReadStatusDao.existsByUserIdAndEventId(currentUserId, activity.getEventId());
        dto.setUnread(!isRead);

        return dto;
    }

    private static VideoActivityDto transform(VideoActivity activity) {
        VideoActivityDto dto = new VideoActivityDto();
        dto.setType(ActivityDto.Type.VIDEO);
        dto.setDuration(activity.getDuration());
        dto.setVideoType(Boolean.TRUE.equals(activity.getIncoming()) ? ActivityDto.CallType.INCOMING : ActivityDto.CallType.OUTGOING);
        return dto;
    }

    private static CallActivityDto transform(CallActivity activity) {
        CallActivityDto dto = new CallActivityDto();
        dto.setType(ActivityDto.Type.CALL);
        dto.setDuration(activity.getDuration());
        dto.setCallType(Boolean.TRUE.equals(activity.getIncoming()) ? ActivityDto.CallType.INCOMING : ActivityDto.CallType.OUTGOING);
        return dto;
    }

    private static InvitationActivityDto transform(InvitationActivity activity) {
        InvitationActivityDto dto = new InvitationActivityDto();
        dto.setType(ActivityDto.Type.INVITATION);
        dto.setStatus(activity.getStatus());
        return dto;
    }

    List<Activity> getRecentActivity(Long userPatientId, Employee employee, Pageable pageable) {
        return activityDao.findByPatientIdAndEmployee(userPatientId, employee, pageable);
    }

    Long countRecentActivity(Long userPatientId, Employee employee) {
        return activityDao.countByPatientIdAndEmployee(userPatientId, employee);
    }

    <T extends ActivityDto> Activity logCallActivity(Long userPatientId, Employee employeeCTM, T activityDto) {
        if (activityDto instanceof CallActivityDto) {
            CallActivityDto callActivityDto = (CallActivityDto) activityDto;
            CallActivity activity = new CallActivity();
            activity.setEmployee(employeeCTM);
            activity.setPatientId(userPatientId);
            activity.setDate(new Date());
            activity.setDuration(callActivityDto.getDuration());
            activity.setIncoming(ActivityDto.CallType.INCOMING.equals(callActivityDto.getCallType()));
            return activityDao.save(activity);
        } else if (activityDto instanceof VideoActivityDto) {
            VideoActivityDto videoActivityDto = (VideoActivityDto) activityDto;
            VideoActivity activity = new VideoActivity();
            activity.setEmployee(employeeCTM);
            activity.setPatientId(userPatientId);
            activity.setDate(new Date());
            activity.setDuration(videoActivityDto.getDuration());
            activity.setIncoming(ActivityDto.CallType.INCOMING.equals(videoActivityDto.getVideoType()));
            return activityDao.save(activity);
        } else {
            throw new PhrException(PhrExceptionType.ACTIVITY_NOT_VALID);
        }
    }
}

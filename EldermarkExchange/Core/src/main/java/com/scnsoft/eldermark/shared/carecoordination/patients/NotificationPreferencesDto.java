package com.scnsoft.eldermark.shared.carecoordination.patients;

import com.scnsoft.eldermark.dao.carecoordination.NotificationType;
import com.scnsoft.eldermark.dao.carecoordination.Responsibility;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pzhurba on 21-Oct-15.
 */
@XmlRootElement
public class NotificationPreferencesDto {
    private Long id;
    private long eventTypeId;
    private String eventType;
    private Responsibility responsibility;
    private List<NotificationType> notificationTypeList = new ArrayList<NotificationType>();
    private boolean canChange = true;
//    private Integer priority;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Responsibility getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(Responsibility responsibility) {
        this.responsibility = responsibility;
    }

    public List<NotificationType> getNotificationTypeList() {
        return notificationTypeList;
    }

    public void setNotificationTypeList(List<NotificationType> notificationTypeList) {
        this.notificationTypeList = notificationTypeList;
    }

    public boolean isCanChange() {
        return canChange;
    }

    public void setCanChange(boolean canChange) {
        this.canChange = canChange;
    }

    public void checkSetAllForSave() {
        if (notificationTypeList.contains(NotificationType.ALL)) {
            notificationTypeList.remove(NotificationType.ALL);
            notificationTypeList.addAll(NotificationType.getAll());
        } else if (responsibility == Responsibility.N) {
            notificationTypeList.clear();
            notificationTypeList.addAll(NotificationType.getAll());
        }
    }

    public void checkSetAllForLoad() {
        if (responsibility == Responsibility.N) {
            notificationTypeList.clear();
            return;
        }
        for (NotificationType type:  NotificationType.getAll()) {
            if (!notificationTypeList.contains(type)){
                return;
            }
        }
        notificationTypeList.clear();
        notificationTypeList.add(NotificationType.ALL);
    }
//
//    public void setPriority(Integer priority) {
//        this.priority = priority;
//    }
//
//    public Integer getPriority() {
//        return priority;
//    }

//    public void addNotificationType(CareTeamMemberNotificationPreferences np) {
////        NotificationTypeDto notificationTypeDto = new NotificationTypeDto();
//        notificationTypeList.add(new NotificationTypeDto(np.getId(),np.getNotificationType()));
//    }
}

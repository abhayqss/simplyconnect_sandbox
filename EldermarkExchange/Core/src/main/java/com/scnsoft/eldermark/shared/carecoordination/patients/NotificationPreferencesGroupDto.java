package com.scnsoft.eldermark.shared.carecoordination.patients;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pzhurba on 21-Oct-15.
 */
@XmlRootElement
public class NotificationPreferencesGroupDto {
    private String name;
    private Integer priority;
    private List<NotificationPreferencesDto> notificationPreferences = new ArrayList<NotificationPreferencesDto>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<NotificationPreferencesDto> getNotificationPreferences() {
        return notificationPreferences;
    }

    public void setNotificationTypeList(List<NotificationPreferencesDto> notificationPreferences) {
        this.notificationPreferences = notificationPreferences;
    }
}

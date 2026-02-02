package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDaoImpl;
import com.scnsoft.eldermark.entity.NotificationPreferences;
import org.springframework.stereotype.Repository;

/**
 * Created by pzhurba on 24-Sep-15.
 */
@Repository
public class NotificationPreferencesDaoImpl extends BaseDaoImpl<NotificationPreferences> implements NotificationPreferencesDao {
    public NotificationPreferencesDaoImpl() {
        super(NotificationPreferences.class);
    }
}

package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDao;
import com.scnsoft.eldermark.entity.EventsProvider;

/**
 * Created by pzhurba on 24-Sep-15.
 */
public interface EventsProviderDao extends BaseDao<EventsProvider> {
    EventsProvider getByLogin(final String login);
}

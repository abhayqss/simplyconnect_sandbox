package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDaoImpl;
import com.scnsoft.eldermark.entity.EventsProvider;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;

/**
 * Created by pzhurba on 24-Sep-15.
 */
@Repository
public class EventsProviderDaoImpl extends BaseDaoImpl<EventsProvider> implements EventsProviderDao {
    public EventsProviderDaoImpl() {
        super(EventsProvider.class);
    }

    @Override
    public EventsProvider getByLogin(final String login) {
        final TypedQuery<EventsProvider> query = entityManager.createQuery("SELECT o FROM EventsProvider o WHERE o.login = :login", entityClass);
        query.setParameter("login", login);
        return query.getSingleResult();
    }
}

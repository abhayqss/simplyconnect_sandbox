package com.scnsoft.eldermark.event.xml.security;

import com.scnsoft.eldermark.event.xml.dao.EventsProviderDao;
import com.scnsoft.eldermark.event.xml.entity.EventsProvider;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EventsProviderDetailsService implements UserDetailsService {

    private final EventsProviderDao eventsProviderDao;

    @Autowired
    public EventsProviderDetailsService(EventsProviderDao eventsProviderDao) {
        this.eventsProviderDao = eventsProviderDao;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        List<EventsProvider> eventsProviders;
        try {
            eventsProviders = eventsProviderDao.getByLogin(login);
        } catch (DataAccessException e) {
            throw new UsernameNotFoundException("Data access error", e);
        }

        if (CollectionUtils.isEmpty(eventsProviders)) {
            throw new UsernameNotFoundException(String.format("No users found [%s]", login));
        }

        if (eventsProviders.size() > 1) {
            throw new UsernameNotFoundException(String.format("Duplicated (login, database) pair [%s]", login));
        }

        var eventsProvider = eventsProviders.get(0);
        return new User(eventsProvider.getLogin(), eventsProvider.getPassword(), new ArrayList<>());
    }
}

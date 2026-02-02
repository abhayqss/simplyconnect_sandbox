package com.scnsoft.eldermark.security;

import com.scnsoft.eldermark.dao.carecoordination.EventsProviderDao;
import com.scnsoft.eldermark.dao.exceptions.MultipleEntitiesFoundException;
import com.scnsoft.eldermark.entity.EventsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import java.util.ArrayList;

/**
 * Created by pzhurba on 27-Oct-15.
 */
public class EventsProviderDetailsService implements UserDetailsService {
    @Autowired
    EventsProviderDao eventsProviderDao;


    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        EventsProvider eventsProvider;

        try {
            eventsProvider = eventsProviderDao.getByLogin(login);
        } catch (MultipleEntitiesFoundException e) {
            throw new UsernameNotFoundException(String.format("Duplicated (login, database) pair [%s]", login));
        } catch (DataAccessException e) {
            throw new UsernameNotFoundException("Data access error", e);
        }

        if (eventsProvider == null) {
            throw new UsernameNotFoundException(String.format("No users found [%s]", login));
        }
        return new User(eventsProvider.getLogin(), eventsProvider.getPassword(), new ArrayList<GrantedAuthority>());
    }


    public static void main(String[] args){
        StandardPasswordEncoder standardPasswordEncoder = new StandardPasswordEncoder();
        System.out.println(standardPasswordEncoder.encode("exchange"));
    }
}

package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Name;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Person;
import com.scnsoft.eldermark.consana.sync.server.model.entity.PersonAddress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Service
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
public class PersonServiceImpl implements PersonService {

    @Override
    public Person updateEmptyFields(Person target, Person source) {
        if (target== null) {
            target = source;
        }
        if (target != null && isEmpty(target.getNames()) && !isEmpty(source.getNames())) {
            for (Name name : source.getNames()){
                name.setPerson(target);
            }
            target.setNames(source.getNames());
        }
        if (target != null && isEmpty(target.getAddresses()) && !isEmpty(target.getNames())){
            for (PersonAddress adr : source.getAddresses()){
                adr.setPerson(target);
            }
            target.setAddresses(source.getAddresses());
        }
        return target;
    }



}

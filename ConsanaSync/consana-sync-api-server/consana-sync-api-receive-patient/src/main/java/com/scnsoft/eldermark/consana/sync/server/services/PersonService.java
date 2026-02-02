package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Person;

public interface PersonService {

    Person updateEmptyFields(Person target, Person source);

}

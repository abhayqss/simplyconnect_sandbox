package com.scnsoft.eldermark.entity.document.facesheet;

import java.util.List;

import com.scnsoft.eldermark.entity.Person;

public interface ContactWithRole {
    String getRole();
    List<Person> getPersons();
    String getNpi();
    String getCommuntiyName();
}

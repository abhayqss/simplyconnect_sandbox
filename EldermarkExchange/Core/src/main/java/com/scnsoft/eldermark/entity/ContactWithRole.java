package com.scnsoft.eldermark.entity;

import java.util.List;

public interface ContactWithRole {
    String getRole();
    List<Person> getPersons();
    String getNpi();
    String getOrganizationName();
}

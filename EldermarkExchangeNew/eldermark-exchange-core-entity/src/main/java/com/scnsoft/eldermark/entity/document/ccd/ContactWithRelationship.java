package com.scnsoft.eldermark.entity.document.ccd;

import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.document.CcdCode;

public interface ContactWithRelationship {
    CcdCode getRelationship();
    Person getPerson();
}

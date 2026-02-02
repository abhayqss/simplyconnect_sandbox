package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.PersonTelecom;
import org.springframework.stereotype.Repository;

/**
 * Created by pzhurba on 24-Sep-15.
 */
@Repository
public class PersonTelecomDaoImpl extends BaseDaoImpl<PersonTelecom> implements PersonTelecomDao {
    public PersonTelecomDaoImpl() {
        super(PersonTelecom.class);
    }


}

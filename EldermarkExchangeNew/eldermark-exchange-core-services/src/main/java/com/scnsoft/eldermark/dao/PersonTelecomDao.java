package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.PersonTelecom;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonTelecomDao extends AppJpaRepository<PersonTelecom, Long>, CustomPersonTelecomDao {

}

package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.SourceDatabaseAddressAndContacts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by averazub on 3/21/2016.
 */
@Repository
public interface CareCoordinationOrgAddressAndContactsDao extends JpaRepository<SourceDatabaseAddressAndContacts, Long>, JpaSpecificationExecutor<SourceDatabaseAddressAndContacts> {

}

package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.phr.AccessRight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author phomal
 * Created on 5/15/2017.
 */
@Repository
public interface AccessRightDao extends JpaRepository<AccessRight, Long> {

    AccessRight findByCode(AccessRight.Code code);

}

package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.phr.RegistrationStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author phomal
 *
 * Created on 10/17/2017.
 */
@Repository
public interface RegistrationStepDao extends JpaRepository<RegistrationStep, Integer> {

}

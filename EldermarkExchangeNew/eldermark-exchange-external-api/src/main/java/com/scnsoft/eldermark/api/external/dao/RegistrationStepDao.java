package com.scnsoft.eldermark.api.external.dao;

import com.scnsoft.eldermark.api.external.entity.RegistrationStep;
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

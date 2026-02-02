package com.scnsoft.eldermark.api.external.dao;

import com.scnsoft.eldermark.dao.VitalSignDao;
import com.scnsoft.eldermark.entity.document.ccd.VitalSignObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * Spring Data version of {@link VitalSignDao VitalSignDao} and {@link VitalSignObservationDao VitalSignObservationDao} repositories.
 *
 * @author phomal
 * Created on 11/8/2017.
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface VitalSignObservationDao extends JpaRepository<VitalSignObservation, Long> {

}

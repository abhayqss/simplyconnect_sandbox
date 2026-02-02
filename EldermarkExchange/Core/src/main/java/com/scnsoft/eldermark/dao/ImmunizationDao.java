package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Immunization;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

/**
 * @deprecated Transition to Spring Data repositories is recommended. Use {@link com.scnsoft.eldermark.dao.healthdata.ImmunizationDao ImmunizationDao} instead.
 */
public interface ImmunizationDao extends ResidentAwareDao<Immunization> {
    List<Immunization> listResidentImmunizations(Collection<Long> residentIds, Pageable pageable);
}

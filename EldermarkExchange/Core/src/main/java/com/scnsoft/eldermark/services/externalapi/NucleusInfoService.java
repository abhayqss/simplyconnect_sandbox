package com.scnsoft.eldermark.services.externalapi;

import com.scnsoft.eldermark.entity.externalapi.NucleusInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * @author phomal
 * Created on 3/16/2018.
 */
public interface NucleusInfoService {
    @Transactional(readOnly = true)
    String findByEmployeeId(Long employeeId);
    @Transactional(readOnly = true)
    String findByResidentId(Long residentId);
    @Transactional(readOnly = true)
    List<NucleusInfo> findByResidentIds(Collection<Long> residentIds);
    @Transactional(readOnly = true)
    List<Long> findResidentIdsByNucleusId(String nucleusId);
    @Transactional(readOnly = true)
    List<Long> findEmployeeIdsByNucleusId(String nucleusId);

    boolean isNucleusIntegrationEnabled();
    String getNucleusPollingAuthToken();
    String getNucleusAuthToken();
    String getNucleusHost();
}

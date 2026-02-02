package com.scnsoft.eldermark.dao.externalapi;

import com.scnsoft.eldermark.entity.externalapi.NucleusDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author phomal
 * Created on 2/13/2018.
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface NucleusDeviceDao extends JpaRepository<NucleusDevice, Long> {

    List<NucleusDevice> getAllByEmployeeId(Long employeeId);
    List<NucleusDevice> getAllByResidentId(Long residentId);

    void deleteByNucleusId(String uuid);

}

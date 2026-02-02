package com.scnsoft.eldermark.dao.externalapi;

import com.scnsoft.eldermark.entity.externalapi.NucleusInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * @author phomal
 * Created on 2/13/2018.
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface NucleusInfoDao extends JpaRepository<NucleusInfo, Long> {

    NucleusInfo findOneByEmployeeId(Long employeeId);
    NucleusInfo findOneByResidentId(Long residentId);
    List<NucleusInfo> findAllByResidentIdIn(Collection<Long> residentIds);

    @Query("select residentId from NucleusInfo where nucleusUserId = :nucleusUserId and residentId is not null")
    List<Long> findResidentIdsByNucleusId(@Param("nucleusUserId") String nucleusUserId);
    @Query("select employeeId from NucleusInfo where nucleusUserId = :nucleusUserId and employeeId is not null")
    List<Long> findEmployeeIdsByNucleusId(@Param("nucleusUserId") String nucleusUserId);

    void deleteByEmployeeId(Long employeeId);
    void deleteByResidentId(Long residentId);

}

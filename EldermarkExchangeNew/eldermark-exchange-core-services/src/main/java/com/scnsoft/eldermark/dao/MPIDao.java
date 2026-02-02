package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.MPI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MPIDao extends AppJpaRepository<MPI, Long> {

    @Query("SELECT m FROM MPI m where m.clientId=:clientId order by m.id")
    List<MPI> getByClientId(@Param("clientId") Long clientId);

    MPI findFirstByPatientIdAndAssigningAuthorityNamespaceAndAssigningAuthorityUniversalAndAssigningAuthorityUniversalType(
            String patientIdentifier, String assigningAuthorityNamespace,
            String assigningAuthorityOid, String assigningAuthorityOidType);

    MPI findFirstByPatientIdAndAssigningAuthorityUniversalAndAndAssigningAuthorityUniversalType(String patientIdentifier,
                                                                                                String assigningAuthorityOid,
                                                                                                String assigningAuthorityOidType);
    boolean existsByClientId(Long clientId);

}

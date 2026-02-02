package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.MPI;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMPIDao extends JpaRepository<MPI, Long> {

    MPI findFirstByPatientIdAndAssigningAuthorityNamespaceAndAssigningAuthorityUniversalAndAssigningAuthorityUniversalType(
            String patientIdentifier, String assigningAuthorityNamespace,
            String assigningAuthorityOid, String assigningAuthorityOidType);

    MPI findFirstByPatientIdAndAssigningAuthorityUniversalAndAndAssigningAuthorityUniversalType(String patientIdentifier,
                                                                                                String assigningAuthorityOid,
                                                                                                String assigningAuthorityOidType);
}

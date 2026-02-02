package com.scnsoft.eldermark.hl7v2.h2;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.scnsoft.eldermark.dao.MPIDao;
import com.scnsoft.eldermark.dao.specification.SpecificationUtils;
import com.scnsoft.eldermark.hl7v2.dao.HL7MessageLogDao;
import com.scnsoft.eldermark.hl7v2.entity.HL7MessageLog;
import com.scnsoft.eldermark.hl7v2.source.HL7v2IntegrationPartner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;


@DatabaseSetup(value = {"/h2/datasets/import.xml", "/h2/datasets/hl7codes.xml"}, type = DatabaseOperation.REFRESH)
public class BaseHL7H2IT extends BaseH2IT {

    @Autowired
    private HL7MessageLogDao hl7MessageLogDao;
    @Autowired
    private MPIDao mpiDao;

    protected HL7MessageLog runValidations(String patientId,
                                         String assigningAuthorityOid,
                                         String assigningAuthorityOidType,
                                         HL7v2IntegrationPartner integrationPartner,
                                         String msgPart) {
        var log = findLog(msgPart);
        assertThat(log.getAdtMessageId()).isNotNull();
        assertThat(log.getResolvedIntegration()).isEqualTo(integrationPartner.name());
        assertThat(log.isSuccess()).isTrue();

        assertThat(log.getAffectedClient1Id()).isNotNull();


        var mpi = mpiDao.getByClientId(log.getAffectedClient1Id()).get(0);

        assertThat(mpi.getPatientId()).isEqualTo(patientId);
        assertThat(mpi.getAssigningAuthorityUniversal()).isEqualTo(assigningAuthorityOid);
        assertThat(mpi.getAssigningAuthorityUniversalType()).isEqualTo(assigningAuthorityOidType);

        assertThat(mpi).isNotNull();
        assertThat(mpi.getClientId()).isNotNull();

        assertThat(log.getAffectedClient1Id()).isEqualTo(mpi.getClientId());

        return log;
    }

    protected HL7MessageLog findLog(String msgPart) {
        return hl7MessageLogDao.findFirst(
                        (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("rawMessage"), SpecificationUtils.wrapWithWildcards(msgPart)),
                        HL7MessageLog.class,
                        Sort.by(Sort.Direction.DESC, "id")
                )
                .orElseThrow();
    }
}

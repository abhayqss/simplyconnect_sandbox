package com.scnsoft.eldermark.hl7v2.processor.patient.mpi;

import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.MPIDao;
import com.scnsoft.eldermark.dao.specification.SpecificationUtils;
import com.scnsoft.eldermark.entity.MPI;
import com.scnsoft.eldermark.entity.MPI_;
import com.scnsoft.eldermark.hl7v2.exception.RegistryPatientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

@Service
public class HL7v2MpiServiceImpl implements HL7v2MpiService {
    private static final Logger logger = LoggerFactory.getLogger(HL7v2MpiServiceImpl.class);

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private MPIDao mpiDao;

    @Override
    @Transactional(readOnly = true)
    public MPI findMPI(MPI mpi) {
        List<MPI> list;

        //uid and uid type will be mandatory
        //when changing default XDS community - MPI af should also be rewritten for default (postgres registry should also be modified)?
        //1. if it is a full match (aa and af(uid and type)) - return it
        //2. if aa and af are present and no match found - check if af is matching default community
        //3. if no af provided - find with null af

        try {
            list = findExactMPI(mpi);
            if (list.size() == 0) {
                list = findMPIFromDefaultCommunity(mpi);
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve MPI from registry patient service", e);
            throw new RegistryPatientException(e);
        }

        return list.size() > 0 ? list.get(0) : null;
    }

    private List<MPI> findMPIFromDefaultCommunity(MPI personIdentifier) {
        if (personIdentifier.getAssigningAuthorityUniversal() == null) {
            return Collections.emptyList();
        }

        var defaultFacility = communityDao.findFirstByOrganizationOidAndXdsDefaultTrue(personIdentifier.getAssigningAuthorityUniversal());
        if (defaultFacility == null) {
            return Collections.emptyList();
        }

        if (personIdentifier.getAssigningFacilityUniversal() != null && !personIdentifier.getAssigningFacilityUniversal().equals(defaultFacility.getOid())) {
            return Collections.emptyList();
        }

        var spec = buildMPIDefaultCommunitySpecification(
                personIdentifier.getPatientId(),
                personIdentifier.getAssigningAuthority(),
                "N",
                defaultFacility.getOid()
        );
        return mpiDao.findAll(spec);
    }

    private List<MPI> findExactMPI(MPI personIdentifier) {
        var spec = buildMPIExactSpecification(
                personIdentifier.getPatientId(),
                personIdentifier.getAssigningAuthority(),
                "N",
                personIdentifier.getAssigningFacilityUniversal()
        );
        return mpiDao.findAll(spec);
    }

    private Specification<MPI> buildMPIDefaultCommunitySpecification(String patientId, String assigningAuthority,
                                                                     String deletePatient, String defaultFacilityOid) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        buildMPICommonPredicate(patientId, assigningAuthority, deletePatient, root, criteriaBuilder),
                        criteriaBuilder.or(
                                criteriaBuilder.isNull(root.get(MPI_.assigningFacilityUniversal)),
                                criteriaBuilder.equal(root.get(MPI_.assigningFacilityUniversal), defaultFacilityOid)
                        )
                );
    }

    private Specification<MPI> buildMPIExactSpecification(String patientId, String assigningAuthority, String deletePatient, String assigningFacilityUniversal) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        buildMPICommonPredicate(patientId, assigningAuthority, deletePatient, root, criteriaBuilder),
                        assigningFacilityUniversal == null ?
                                criteriaBuilder.isNull(root.get(MPI_.assigningFacilityUniversal)) :
                                criteriaBuilder.equal(root.get(MPI_.assigningFacilityUniversal), assigningFacilityUniversal)
                );
    }

    private Predicate buildMPICommonPredicate(String patientId, String assigningAuthority, String deletePatient,
                                              Root<MPI> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                criteriaBuilder.equal(root.get(MPI_.patientId), patientId),
                criteriaBuilder.like(root.get(MPI_.assigningAuthority), SpecificationUtils.wrapWithWildcards(assigningAuthority)),
                criteriaBuilder.equal(root.get(MPI_.deleted), deletePatient)
        );
    }

    @Override
    @Transactional
    public MPI save(MPI mpi) {
        return mpiDao.save(mpi);
    }
}

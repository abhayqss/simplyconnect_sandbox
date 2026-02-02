package com.scnsoft.eldermark.services.merging;

import com.scnsoft.eldermark.dao.MPIDao;
import com.scnsoft.eldermark.entity.MPI;
import com.scnsoft.eldermark.entity.Resident;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author phomal
 * Created on 4/12/2017.
 */
@Service
public class MPIServiceImpl implements MPIService {

    @Autowired
    private MPIDao mpiDao;

    @Value("${home.community.id}")
    private String assigningAuthorityId;

    @Value("${home.community.id.namespace}")
    private String assigningAuthorityNamespace;

    @Override
    @Transactional
    public List<Long> listMergedResidents(Long residentId) {
        return mpiDao.listMergedResidents(residentId);
    }

    @Override
    @Transactional
    public List<Long> listResidentWithMergedResidents(Long residentId) {
        List<Long> result = listMergedResidents(residentId);
        if (!result.contains(residentId)) {
            result.add(residentId);
        }
        return result;
    }

    @Override
    public List<Long> listMergedResidents(Collection<Long> residentIds) {
        // TODO benchmark and optimize(?)
        List<Long> mergedResidentIds = new ArrayList<Long>();
        for (Long residentId : residentIds) {
            mergedResidentIds.addAll(listMergedResidents(residentId));
        }
        return mergedResidentIds;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public MPI createMPI(Long residentId, String survivingPatientId) {
        MPI personIdentifier = new MPI();

        //personIdentifier.setRegistryPatientId(UUID.randomUUID().toString());
        personIdentifier.setMerged(survivingPatientId == null ? "N" : "Y");
        personIdentifier.setSurvivingPatientId(survivingPatientId);
        personIdentifier.setDeleted("N");
        personIdentifier.setPatientId(residentId.toString());
        personIdentifier.setResidentId(residentId);
        personIdentifier.setAssigningAuthorityUniversalType("ISO");
        personIdentifier.setAssigningAuthorityUniversal(assigningAuthorityId);
        personIdentifier.setAssigningAuthorityNamespace(assigningAuthorityNamespace);
        personIdentifier.setAssigningAuthority(assigningAuthorityNamespace + "&" + assigningAuthorityId + "&ISO");

        return mpiDao.create(personIdentifier);
    }

    @Override
    public void deleteMPIByResidentId(Long residentId) {
        final List<MPI> mpis = mpiDao.getByResidentId(residentId);
        for (MPI mpi : mpis) {
            mpiDao.delete(mpi);
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void createOrUpdateMpi(Resident mergedResident, Resident survivingResident) {
        String survivingPatientId = (survivingResident == null) ? null : String.valueOf(survivingResident.getId());
        Set<MPI> mpis = mergedResident.getMpi();

        if (!CollectionUtils.isEmpty(mpis)) {
            for (MPI mpi : mpis) {
                if ((StringUtils.equals(mpi.getSurvivingPatientId(), survivingPatientId) && mpi.getMerged().equals("Y"))
                        || (survivingPatientId == null && mpi.getSurvivingPatientId() == null && mpi.getMerged().equals("N"))) {
                    return;
                }

                mpi.setMerged(survivingPatientId == null ? "N" : "Y");
                mpi.setSurvivingPatientId(survivingPatientId);
                mpiDao.merge(mpi);
            }
        } else {
            MPI mpi = createMPI(mergedResident.getId(), survivingPatientId);
            mergedResident.getMpi().add(mpi);
        }
    }

    @Override
    public List<Long> listResidentsAndMergedResidents(Long databaseId) {
        return mpiDao.listResidentsAndMergedResidents(databaseId);
    }

    @Override
    public List<MPI> getByResidentId(Long residentId) {
        return mpiDao.getByResidentId(residentId);
    }

    @Override
    public MPI findMpiForResidentOrMergedAndDatabaseOid(Long residentId, String oid) {
        MPI mpi = fetchMpi(residentId, oid);
        if (!isMpiPresent(mpi)) {
            mpi = findMpiOfMerged(residentId, oid);
        }

        if (!isMpiPresent(mpi)) {
            return null;
        }
        return mpi;
    }

    private MPI fetchMpi(Long residentId, String oid) {
        final List<MPI> mpiList = getByResidentId(residentId);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(mpiList)) {
            for (MPI mpi : mpiList) {
                if (oid.equals(mpi.getAssigningAuthorityUniversal())) {
                    return mpi;
                }
            }
        }
        return null;
    }

    private MPI findMpiOfMerged(Long residentId, String oid) {
        List<Long> mergedResidentIds = listMergedResidents(residentId);
        for (Long mergedResidentId : mergedResidentIds) {
            MPI mpi = fetchMpi(mergedResidentId, oid);
            if (isMpiPresent(mpi)) {
                return mpi;
            }
        }
        return null;
    }

    private boolean isMpiPresent(MPI mpi) {
        return mpi != null && StringUtils.isNotBlank(mpi.getPatientId());
    }


//    @Override
//    public String getAaUniversalByResidentId(Long residentId) {
//        return mpiDao.getAaUniversalByResidentId(residentId);
//    }

}

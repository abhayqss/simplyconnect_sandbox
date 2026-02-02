package com.scnsoft.eldermark.services.merging;

import com.scnsoft.eldermark.dao.MPIMergedResidentsDao;
import com.scnsoft.eldermark.entity.MpiMergedResidents;
import com.scnsoft.eldermark.entity.Resident;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author phomal
 * Created on 4/12/2017.
 */
@Service
public class MpiMergedResidentsServiceImpl implements MpiMergedResidentsService {

    @Autowired
    private MPIMergedResidentsDao mpiMergedResidentsDao;

    @Override
    @Transactional
    public List<Long> listProbablyMatchedResidents(Long residentId) {
        return mpiMergedResidentsDao.listProbablyMatchedResidents(residentId);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteMpiMergedResidents(MpiMergedResidents mpiMergedResidents) {
        mpiMergedResidentsDao.delete(mpiMergedResidents);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void createOrUpdateMpiMergedResidents(Resident mergedResident, Resident survivingResident) {
        Set<MpiMergedResidents> mainResidents = mergedResident.getMainResidents();
        MpiMergedResidents mainResident = getIfExists(mainResidents, survivingResident);
        if (mainResident != null) {
            mainResident.setMerged(true);
            mainResident.setProbablyMatched(false);
            mainResident.setMergedManually(true);
            mpiMergedResidentsDao.merge(mainResident);
            return;
        }

        MpiMergedResidents mpiMergedResidents = new MpiMergedResidents();
        mpiMergedResidents.setMerged(true);
        mpiMergedResidents.setMergedManually(true);
        mpiMergedResidents.setMergedAutomatically(false);
        mpiMergedResidents.setProbablyMatched(false);
        mpiMergedResidents.setSurvivingResident(survivingResident);
        mpiMergedResidents.setMergedResident(mergedResident);
        mpiMergedResidents = mpiMergedResidentsDao.create(mpiMergedResidents);

        mergedResident.getMainResidents().add(mpiMergedResidents);
        survivingResident.getSecondaryResidents().add(mpiMergedResidents);
    }

    @Override
    @Transactional
    public boolean areMergedAutomatically(Resident resident, Long survivingResidentId) {
        if (CollectionUtils.isEmpty(resident.getMainResidents()) || survivingResidentId == null) {
            return false;
        }

        for (MpiMergedResidents mpiMergedResidents : resident.getMainResidents()) {
            if (survivingResidentId.equals(mpiMergedResidents.getSurvivingResident().getId())) {
                return mpiMergedResidents.isMergedAutomatically();
            }
        }

        return false;
    }

    private MpiMergedResidents getIfExists(Set<MpiMergedResidents> mainResidents, Resident survivingResident) {
        for (MpiMergedResidents mainResident : mainResidents) {
            if (survivingResident.equals(mainResident.getSurvivingResident())) {
                return mainResident;
            }
        }
        return null;
    }

}

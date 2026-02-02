package com.scnsoft.eldermark.services.merging;

import com.scnsoft.eldermark.entity.MpiMergedResidents;
import com.scnsoft.eldermark.entity.Resident;

import java.util.List;

/**
 * @author phomal
 * Created on 4/12/2017.
 */
public interface MpiMergedResidentsService {

    List<Long> listProbablyMatchedResidents(Long residentId);

    void deleteMpiMergedResidents(MpiMergedResidents mpiMergedResidents);

    void createOrUpdateMpiMergedResidents(Resident mergedResident, Resident survivingResident);

    boolean areMergedAutomatically(Resident resident, Long survivingResidentId);

}

package com.scnsoft.eldermark.services.merging;

import com.scnsoft.eldermark.entity.MPI;
import com.scnsoft.eldermark.entity.Resident;

import java.util.Collection;
import java.util.List;

/**
 * @author phomal
 * Created on 4/12/2017.
 */
public interface MPIService {

    /**
     * Returns a list containing ids of merged residents without residentId itself.
     * @param residentId
     * @return
     */
    List<Long> listMergedResidents(Long residentId);

    /**
     * Returns a list containing residentId along with ids of merged residents.
     * @param residentId
     * @return
     */
    List<Long> listResidentWithMergedResidents(Long residentId);

    List<Long> listMergedResidents(Collection<Long> residentIds);

    MPI createMPI(Long residentId, String survivingPatientId);

    void deleteMPIByResidentId(Long residentId);

    void createOrUpdateMpi(Resident resident, Resident survivingResident);

    List<Long> listResidentsAndMergedResidents(Long databaseId);

    List<MPI> getByResidentId(Long residentId);
//    String getAaUniversalByResidentId(Long residentId);

    MPI findMpiForResidentOrMergedAndDatabaseOid(Long residentId, String oid);

}

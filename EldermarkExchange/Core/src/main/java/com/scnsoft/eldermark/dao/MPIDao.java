package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.MPI;

import java.util.List;

public interface MPIDao extends BaseDao<MPI> {
    List<Long> listMergedResidents(Long residentId);

    List<MPI> getByResidentId(Long residentId);

    String getAaUniversalByResidentId(Long residentId);

    List<Long> listResidentsAndMergedResidents(Long databaseId);
}

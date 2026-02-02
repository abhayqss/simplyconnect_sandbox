package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.model.entity.MPI;

import java.util.List;

public interface MPIService {

    List<MPI> getAllByResidentId(Long residentId);
}

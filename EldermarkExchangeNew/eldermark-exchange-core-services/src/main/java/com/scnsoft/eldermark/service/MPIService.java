package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.MPI;

public interface MPIService {

    boolean existsMPI(Client client);

    MPI createMPI(Client client);

}
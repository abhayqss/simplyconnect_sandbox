package com.scnsoft.eldermark.hl7v2.processor.patient.mpi;

import com.scnsoft.eldermark.entity.MPI;

public interface HL7v2MpiService {

    MPI findMPI(MPI mpi);

    MPI save(MPI mpi);
}

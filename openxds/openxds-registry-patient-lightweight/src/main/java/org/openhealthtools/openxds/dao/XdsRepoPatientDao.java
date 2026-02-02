package org.openhealthtools.openxds.dao;

import org.openhealthtools.openxds.entity.Resident;


public interface XdsRepoPatientDao {
    Resident save(Resident resident);

    Resident saveAndFlush(Resident resident);

    Resident findOne(Long patientRepoId);
}

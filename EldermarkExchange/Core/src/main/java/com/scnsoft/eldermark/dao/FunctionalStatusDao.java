package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.FunctionalStatus;

public interface FunctionalStatusDao extends ResidentAwareDao<FunctionalStatus> {
    FunctionalStatus getResidentFunctionalStatus(Long residentId);
}

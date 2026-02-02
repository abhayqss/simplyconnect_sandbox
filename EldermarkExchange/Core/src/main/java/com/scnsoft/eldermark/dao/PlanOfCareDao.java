package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.PlanOfCare;

public interface PlanOfCareDao extends ResidentAwareDao<PlanOfCare> {
    PlanOfCare getResidentPlanOfCare(Long residentId);
}

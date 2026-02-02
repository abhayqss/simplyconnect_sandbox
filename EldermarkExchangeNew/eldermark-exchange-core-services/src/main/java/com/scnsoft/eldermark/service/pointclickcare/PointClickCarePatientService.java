package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.entity.Client;

public interface PointClickCarePatientService {

    Client createOrUpdateClient(String pccOrgUuid, Long pccPatientId);
}

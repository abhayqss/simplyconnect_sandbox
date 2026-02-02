package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dto.pointclickcare.projection.PccClientMatchProjection;
import com.scnsoft.eldermark.entity.Client;

import java.util.Collection;
import java.util.Map;

public interface PointClickCarePatientMatchService {

    boolean match(Client client);

    Map<Long, Long> match(Collection<PccClientMatchProjection> clients);

}

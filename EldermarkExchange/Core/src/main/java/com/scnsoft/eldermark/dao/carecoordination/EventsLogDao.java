package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDao;
import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.entity.EventsLogEntity;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.services.carecoordination.CareCoordinationResidentFilter;
import com.scnsoft.eldermark.shared.carecoordination.patients.PatientsFilterDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by pzhurba on 23-Oct-15.
 */
public interface EventsLogDao extends BaseDao<EventsLogEntity> {



}

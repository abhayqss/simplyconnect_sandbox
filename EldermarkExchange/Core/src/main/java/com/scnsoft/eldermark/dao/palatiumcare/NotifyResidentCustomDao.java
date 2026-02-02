package com.scnsoft.eldermark.dao.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.NotifyResident;
import com.scnsoft.eldermark.shared.palatiumcare.resident.NotifyResidentFilter;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotifyResidentCustomDao {

    List<NotifyResident> getCareTeamMemberResidentsByEmployeeId(Long employeeId, NotifyResidentFilter notifyResidentFilter, Pageable pageable);

    List<NotifyResident> getCommunityResidentsByEmployeeId(Long employeeId, NotifyResidentFilter notifyResidentFilter, Pageable pageable);
}

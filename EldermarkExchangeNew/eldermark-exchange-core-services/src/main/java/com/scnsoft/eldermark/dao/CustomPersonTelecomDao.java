package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.PersonTelecom;

import java.util.List;
import java.util.Map;

public interface CustomPersonTelecomDao {
    Map<Long, List<PersonTelecom>> findAllByClientIdIn(List<Long> clientIds);

    Map<Long, List<PersonTelecom>> findClientIdPersonTelecomsByCtmEmployeeIdIn(List<Long> employeeIds);

    Map<Long, List<PersonTelecom>> findCommunityIdPersonTelecomsByCtmEmployeeIdIn(List<Long> employeeIds);
}

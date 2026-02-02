package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.LinkedEmployeeIdDto;
import com.scnsoft.eldermark.entity.LinkedEmployees;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;


@Repository
public class LinkedEmployeesDaoImpl extends BaseDaoImpl<LinkedEmployees> implements LinkedEmployeesDao  {
    public LinkedEmployeesDaoImpl() {
        super(LinkedEmployees.class);
    }


    @Override
    public List<Long> getLinkedEmployeeIds(Long employeeId) {
        TypedQuery<LinkedEmployeeIdDto> query = entityManager.createNamedQuery("exec__find_linked_employees", LinkedEmployeeIdDto.class);
        query.setParameter("employeeId", employeeId);

        List<Long> linkedEmployeeIds = new ArrayList<Long>();
        for (LinkedEmployeeIdDto e : query.getResultList()) {
            linkedEmployeeIds.add(e.longValue());
        }

        return linkedEmployeeIds;
    }

    @Override
    public void deleteLinkedEmployee(Long linkedEmployeeIdToRemove, Long currentEmployeeId) {
        Query query = entityManager.createNamedQuery("exec__delete_linked_employee");
        query.setParameter("currentEmployeeId", currentEmployeeId);
        query.setParameter("employeeId", linkedEmployeeIdToRemove);
        query.executeUpdate();
    }
}

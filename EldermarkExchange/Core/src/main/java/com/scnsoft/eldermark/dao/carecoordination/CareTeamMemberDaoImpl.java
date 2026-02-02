package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDaoImpl;
import com.scnsoft.eldermark.entity.CareTeamMember;
import com.scnsoft.eldermark.entity.Employee;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;

/**
 * Created by pzhurba on 24-Sep-15.
 */
@Repository
public class CareTeamMemberDaoImpl extends BaseDaoImpl<CareTeamMember> implements CareTeamMemberDao {
    public CareTeamMemberDaoImpl() {
        super(CareTeamMember.class);
    }

    @Override
    public void deleteCareTeamMembersForEmployee(Employee employee) {
        final TypedQuery<CareTeamMember> query = entityManager.createQuery("SELECT o from CareTeamMember o WHERE o.employee = :employee", CareTeamMember.class);
        query.setParameter("employee", employee);
        for (CareTeamMember ctm : query.getResultList()) {
            delete(ctm.getId());
        }
    }

    @Override
    public Long getEmployeeId(Long id) {
        final TypedQuery<Long> query = entityManager.createQuery("SELECT c.employee.id from CareTeamMember c WHERE c.id = :id", Long.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public Employee getEmployee(Long id) {
        final TypedQuery<Employee> query = entityManager.createQuery("SELECT c.employee from CareTeamMember c WHERE c.id = :id", Employee.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }
}

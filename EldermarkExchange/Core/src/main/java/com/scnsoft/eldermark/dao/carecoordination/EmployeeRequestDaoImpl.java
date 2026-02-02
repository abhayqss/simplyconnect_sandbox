package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDaoImpl;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeRequest;
import com.scnsoft.eldermark.entity.EmployeeRequestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

/**
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 24-Sep-15.
 */
@Repository
public class EmployeeRequestDaoImpl extends BaseDaoImpl<EmployeeRequest> implements EmployeeRequestDao {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeRequestDaoImpl.class);

    public EmployeeRequestDaoImpl() {
        super(EmployeeRequest.class);
    }


    @Override
    public EmployeeRequest getByToken(String token, EmployeeRequestType type) {
        final TypedQuery<EmployeeRequest> query = entityManager.createQuery(
                "Select o from EmployeeRequest o WHERE o.token = :token AND o.tokenType = :tokenType", entityClass);
        query.setParameter("token", token);
        query.setParameter("tokenType", type);
        return query.getSingleResult();
    }

    @Override
    public EmployeeRequest getByTargetEmployee(Employee employee, EmployeeRequestType type) {
        final TypedQuery<EmployeeRequest> query = entityManager.createQuery(
                "Select o from EmployeeRequest o WHERE o.targetEmployee = :employee AND o.tokenType = :tokenType", entityClass);
        query.setParameter("employee", employee);
        query.setParameter("tokenType", type);
        query.setMaxResults(1);
        return query.getSingleResult();
    }

//    @Override
//    public EmployeeRequest getByTargetEmployee(Long employeeId, EmployeeRequestType type) {
//        final TypedQuery<EmployeeRequest> query = entityManager.createQuery(
//                "Select o from EmployeeRequest o WHERE o.targetEmployee.id = :employeeId AND o.tokenType = :tokenType", entityClass);
//        query.setParameter("employeeId", employeeId);
//        query.setParameter("tokenType", type);
//        query.setMaxResults(1);
//        return query.getSingleResult();
//    }

    @Override
    public boolean existsByTargetEmployee(Employee employee, EmployeeRequestType type) {
        final TypedQuery<EmployeeRequest> query = entityManager.createQuery(
                "Select o from EmployeeRequest o WHERE o.targetEmployee = :employee AND o.tokenType = :tokenType", entityClass);
        query.setParameter("employee", employee);
        query.setParameter("tokenType", type);
        query.setMaxResults(1);
        return query.getResultList().size() == 1;
    }

    @Override
    public List<EmployeeRequest> getExpiredRequests(Date expirationDate, final EmployeeRequestType type) {
        final TypedQuery<EmployeeRequest> query = entityManager.createQuery(
                "Select o from EmployeeRequest o WHERE o.createdDateTime < :expirationDate AND o.tokenType = :tokenType", entityClass);

        query.setParameter("expirationDate", expirationDate);
        query.setParameter("tokenType", type);

        return query.getResultList();
    }

    @Override
    public int deleteByEmployee(Employee employee) {
        final Query query = entityManager.createQuery("delete from EmployeeRequest where targetEmployee = :employee");
        query.setParameter("employee", employee);
        return query.executeUpdate();
    }

}

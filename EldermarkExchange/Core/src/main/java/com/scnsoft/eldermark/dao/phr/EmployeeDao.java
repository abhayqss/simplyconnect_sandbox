package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * @author phomal
 * Created on 1/30/2018.
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface EmployeeDao extends JpaRepository<Employee, Long> {

    @Query("SELECT communityId FROM Employee WHERE id = :id")
    Long getCommunityIdById(@Param("id") Long id);

    Page<Employee> findByDatabaseIdAndStatus(@Param("databaseId") long databaseId, @Param("status") EmployeeStatus status, Pageable pageable);

    Page<Employee> findByCommunityIdAndStatus(@Param("communityId") long communityId, @Param("status") EmployeeStatus status, Pageable pageable);

    Page<Employee> findByDatabaseIdInAndStatus(@Param("databaseIds") Collection<Long> ids, @Param("status") EmployeeStatus status, Pageable pageable);

}

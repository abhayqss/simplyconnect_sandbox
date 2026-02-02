package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.UserMobile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Deprecated
public interface UserMobileDao extends JpaRepository<UserMobile, Long> {

    boolean existsByEmployeeAndClientId(Employee employee, Long clientId);

    boolean existsByEmployeeAndClientCommunityId(Employee employee, Long communityId);

    Optional<ClientIdAware> findFirstByEmployeeId(long employeeId);

}

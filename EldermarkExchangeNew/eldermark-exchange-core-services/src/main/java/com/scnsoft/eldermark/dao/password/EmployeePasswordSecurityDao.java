package com.scnsoft.eldermark.dao.password;

import com.scnsoft.eldermark.entity.password.EmployeePasswordSecurity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeePasswordSecurityDao extends JpaRepository<EmployeePasswordSecurity, Long> {
}

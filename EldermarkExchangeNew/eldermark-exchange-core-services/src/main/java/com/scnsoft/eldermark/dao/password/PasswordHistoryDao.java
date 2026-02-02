package com.scnsoft.eldermark.dao.password;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.scnsoft.eldermark.entity.password.PasswordHistory;

public interface PasswordHistoryDao extends JpaRepository<PasswordHistory, Long> {

    List<PasswordHistory> findAllByEmployeeId(Long employeeId);
    
    @Procedure(name="enable_password_history")
    void enablePasswordHistory(@Param("databaseId") Long organizationId);

    List<PasswordHistory> findAllByEmployeeIdOrderByIdDesc(Long employeeId, Pageable limit);
}

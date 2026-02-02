package com.scnsoft.eldermark.dao.password;

import com.scnsoft.eldermark.entity.password.PasswordHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PasswordHistoryDao extends JpaRepository<PasswordHistory, Long>, JpaSpecificationExecutor<PasswordHistory> {

    @Query("DELETE FROM PasswordHistory ph WHERE ph.id in (select ph1.id from PasswordHistory ph1 join ph1.employee e where e.databaseId = :databaseId)")
    @Modifying(clearAutomatically = true)
    @Transactional
    void clearPasswordHistory(@Param("databaseId") Long databaseId);

    List<PasswordHistory> findAllByEmployeeId(Long employeeId);

    @Procedure(name="enable_password_history")
    @Modifying(clearAutomatically = true)
    @Transactional
    void enablePasswordHistory(@Param("databaseId") Long databaseId);

    List<PasswordHistory> findAllByEmployeeIdOrderByIdDesc(Long employeeId, Pageable limit);
}

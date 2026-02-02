package com.scnsoft.eldermark.dao.password;

import com.scnsoft.eldermark.entity.password.EmployeePasswordSecurity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
public interface EmployeePasswordSecurityDao extends JpaRepository<EmployeePasswordSecurity, Long>, JpaSpecificationExecutor<EmployeePasswordSecurity> {

    @Query("UPDATE EmployeePasswordSecurity eps SET eps.failedLogonsCount=0 WHERE eps.employee.id=:employeeId")
    @Modifying(clearAutomatically = false)
    @Transactional
    void resetFailAttempts(@Param("employeeId") Long employeeId);

    @Query("UPDATE EmployeePasswordSecurity eps SET eps.failedLogonsCount = eps.failedLogonsCount + 1 WHERE eps.employee.id=:employeeId")
    @Modifying(clearAutomatically = true)
    @Transactional
    void increaseFailAttemptsCounter(@Param("employeeId") Long employeeId);

    @Query("UPDATE EmployeePasswordSecurity eps SET eps.changePasswordTime = :changePasswordTime WHERE eps.employee.id=:employeeId")
    @Modifying(clearAutomatically = true)
    @Transactional
    void updatePasswordChangedDate(@Param("employeeId") Long employeeId, @Param("changePasswordTime")Date changePasswordTime);

    @Query("UPDATE EmployeePasswordSecurity eps SET eps.lockedTime = :lockedTime, eps.locked=true WHERE eps.employee.id=:employeeId")
    @Modifying(clearAutomatically = true)
    @Transactional
    void lockEmployeeAccount(@Param("employeeId") Long employeeId, @Param("lockedTime") Date lockedTime);

    @Query("UPDATE EmployeePasswordSecurity eps SET eps.lockedTime = null, eps.failedLogonsCount=0, eps.locked=false WHERE eps.employee.id=:employeeId")
    @Modifying(clearAutomatically = false)
    @Transactional
    void unlockEmployeeAccount(@Param("employeeId") Long employeeId);

    EmployeePasswordSecurity findEmployeePasswordSecurityByEmployee_Id(Long employeeId);

    @Query("UPDATE EmployeePasswordSecurity eps SET eps.changePasswordTime = null WHERE eps.id in (select eps1.id from EmployeePasswordSecurity eps1 join eps1.employee e where e.databaseId = :databaseId)")
    @Modifying(clearAutomatically = true)
    @Transactional
    void resetPasswordChangedTime(@Param("databaseId") Long databaseId);

    @Query("UPDATE EmployeePasswordSecurity eps SET eps.changePasswordTime = :changePasswordTime WHERE eps.id in (select eps1.id from EmployeePasswordSecurity eps1 join eps1.employee e where e.databaseId = :databaseId)")
    @Modifying(clearAutomatically = true)
    @Transactional
    void setPasswordChangedTime(@Param("databaseId") Long databaseId, @Param("changePasswordTime") Date changePasswordTime);
}

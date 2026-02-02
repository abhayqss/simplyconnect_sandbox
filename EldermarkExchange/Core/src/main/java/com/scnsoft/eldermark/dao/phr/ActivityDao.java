package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.phr.Activity;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.phr.InvitationActivity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author phomal
 * Created on 5/3/2016.
 */
@Repository
public interface ActivityDao extends JpaRepository<Activity, Long> {

    <T extends Activity> List<T> findByPatientIdAndEmployee(Long patientId, Employee employee, Pageable pageable);

    Long countByPatientIdAndEmployee(Long patientId, Employee employee);

    @Query("select ia from InvitationActivity ia where ia.employee = :employee and ia.status = :status")
    List<InvitationActivity> findInvitationActivitiesByEmployeeAndStatus(@Param("employee") Employee employee,
                                                                         @Param("status") InvitationActivity.Status status);

    @Modifying
    @Query("delete from Activity where employee = :employee")
    void deleteByEmployee(@Param("employee") Employee employee);

}

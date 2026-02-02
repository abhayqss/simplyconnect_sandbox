package com.scnsoft.eldermark.dao.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.Alert;
import com.scnsoft.eldermark.entity.palatiumcare.AlertStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("notifyAlertDao")
public interface AlertDao extends PagingAndSortingRepository<Alert, Long> {

    //@todo: change to HQL
    @Modifying
    @Query(value = "UPDATE PalCare_Alert SET alert_status = :status, responder_id = :userId WHERE id = :id", nativeQuery = true)
    void changeAlertStatus(@Param("id") Long id, @Param("userId") Long userId, @Param("status") String status);

    @Query(value = "FROM NotifyAlert alert WHERE alert.status='TAKEN' OR alert.status='NOT_TAKEN_YET' ORDER BY alert.status")
    List<Alert> loadAlertList();

}

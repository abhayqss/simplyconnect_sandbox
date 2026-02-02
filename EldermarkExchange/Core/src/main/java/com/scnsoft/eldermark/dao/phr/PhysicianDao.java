package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.phr.Physician;
import com.scnsoft.eldermark.entity.phr.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author phomal
 * Created on 5/29/2017.
 */
@Repository
public interface PhysicianDao extends JpaRepository<Physician, Long> {

    Physician getByUserMobileId(Long userId);

    Physician getByUserMobileAndVerifiedIsTrue(User user);

    List<Physician> findAllByDiscoverableTrueAndVerifiedTrue();

    Physician findByIdAndDiscoverableTrueAndVerifiedTrue(Long id);

    @Query("SELECT id FROM Physician p WHERE p.employee.id = :employeeId")
    Long getIdByEmployeeId(@Param("employeeId") Long employeeId);

}

package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.phr.RegistrationApplication;
import com.scnsoft.eldermark.entity.phr.UserResidentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author averazub
 * @author phomal
 * Created on 1/11/2017.
 */
@Repository
// TODO make sure that the repository methods aren't called from non-transactional context before uncommenting
//@Transactional(propagation = Propagation.MANDATORY)
public interface UserResidentRecordsDao extends JpaRepository<UserResidentRecord, Long> {

    List<UserResidentRecord> getByUserId(Long userId);

    Long countByUserIdAndCurrentIsTrue(Long userId);

    @Query("SELECT urr FROM UserResidentRecord urr where urr.userId = :userId AND urr.current = true")
    List<UserResidentRecord> getActiveByUserId(@Param("userId") Long userId);

    @Query("SELECT urr.residentId FROM UserResidentRecord urr where urr.userId = :userId AND urr.current = true")
    List<Long> getActiveResidentIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT urr.residentId FROM UserResidentRecord urr where urr.userId = :userId")
    List<Long> getAllResidentIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT urr.providerId FROM UserResidentRecord urr where urr.userId = :userId AND urr.current = true")
    Set<Long> getActiveProviderIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT urr.providerId FROM UserResidentRecord urr where urr.userId = :userId")
    Set<Long> getAllProviderIdsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM UserResidentRecord u where u.userId = :userId AND u.residentId in (:residentIds)")
    void deleteUnusedRecords(@Param("userId") Long userId, @Param("residentIds") Collection<Long> residentIds);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserResidentRecord u set u.current=true where u.userId = :userId AND u.residentId =:residentId")
    int setCurrentRecord(@Param("userId") Long userId, @Param("residentId") Long residentId);

    @Modifying//(clearAutomatically = true)
    @Query("UPDATE UserResidentRecord u set u.current = true where u.userId = :userId")
    int setCurrentRecordsAll(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserResidentRecord u set u.current=false where u.userId = :userId")
    int dropCurrentRecordForUser(@Param("userId") Long userId);

    UserResidentRecord getFirstByResidentId(Long residentId);

    List<UserResidentRecord> getAllByResidentId(@Param("residentId") Long residentId);

    List<UserResidentRecord> getAllByResidentIdIn(@Param("residentIds") Collection<Long> residentIds);

    @Query("SELECT urr.userId FROM UserResidentRecord urr where urr.residentId = :residentId and urr.userId is not null")
    List<Long> getAllUserIdsByResidentId(@Param("residentId") Long residentId);

    @Query("SELECT urr.userId FROM UserResidentRecord urr where urr.residentId IN (:residentIds) and urr.userId is not null")
    List<Long> getAllUserIdsByResidentIdIn(@Param("residentIds") Collection<Long> residentIds);

    /**
     * Check if any residents were found during Sign Up as Consumer and update User-Resident records
     * to make them active health data providers of the specified User.
     */
    @Modifying
    @Query("UPDATE UserResidentRecord u SET u.current = TRUE, u.userId = :userId WHERE u.userRegistrationApplication = :application AND u.userId IS NULL")
    void activateRecords(@Param("application") RegistrationApplication application, @Param("userId") Long userId);

}

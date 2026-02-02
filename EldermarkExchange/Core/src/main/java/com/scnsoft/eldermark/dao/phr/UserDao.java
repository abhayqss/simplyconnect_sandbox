package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.phr.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author averazub
 * @author phomal
 *
 * Created on 12/27/2016.
 */
@Repository
public interface UserDao extends JpaRepository<User, Long> {

    @Deprecated
    List<User> findUsersBySsnAndEmailAndPhone(@Param("ssn") String ssn, @Param("email") String email, @Param("phone") String phone);

    List<User> findUsersByResidentId(Long residentId);

    @Deprecated
    List<User> findUsersByEmailAndPhone(@Param("email") String email, @Param("phone") String phone);

    User findUserByDatabaseAndEmailNormalizedAndAutocreatedIsFalse(@Param("database") Database database,
                                                                   @Param("emailNormalized") String emailNormalized);

    @Query("SELECT u FROM User u WHERE u.emailNormalized = :email AND u.phoneNormalized = :phone AND u.ssn IS NULL AND u.autocreated <> true")
    List<User> findUsersByEmailAndPhoneNormalizedAndSsnIsNull(@Param("email") String emailNormalized, @Param("phone") String phoneNormalized);

    @Query("SELECT u FROM User u WHERE u.emailNormalized = :email AND u.phoneNormalized = :phone AND u.autocreated <> true")
    List<User> findUsersByEmailAndPhoneNormalized(@Param("email") String emailNormalized, @Param("phone") String phoneNormalized);

    @Query("SELECT u FROM User u WHERE u.ssn = :ssn AND u.emailNormalized = :email AND u.phoneNormalized = :phone AND u.autocreated <> true")
    List<User> findUsersBySsnAndEmailAndPhoneNormalized(@Param("ssn") String ssn, @Param("email") String emailNormalized, @Param("phone") String phoneNormalized);

    List<User> findUsersByDatabaseIdAndEmailNormalizedAndPhoneNormalized(@Param("databaseId") Long databaseId,
                                                                         @Param("email") String emailNormalized,
                                                                         @Param("phone") String phoneNormalized);

    @SuppressWarnings("SpringDataMethodInconsistencyInspection")
    @Query("SELECT u FROM User u WHERE u.database = :database AND u.ssn = :ssn AND u.emailNormalized = :email AND u.phoneNormalized = :phone " +
            "AND u.firstName = :firstName AND u.lastName = :lastName AND u.employee IS NULL")
    List<User> findUsersByData(@Param("database") Database database,
                               @Param("ssn") String ssn,
                               @Param("email") String emailNormalized,
                               @Param("phone") String phoneNormalized,
                               @Param("firstName") String firstName,
                               @Param("lastName") String lastName);

    @Query("SELECT CASE WHEN count(u.id)>0 THEN true ELSE false END FROM User u WHERE u.id=:userId and u.tokenEncoded=:token")
    Boolean validateToken(@Param("userId") Long userId, @Param("token") String token);

    User getFirstByEmployee(@Param("employee") Employee employee);

    List<User> getAllByEmployee(@Param("employee") Employee employee);

    List<User> getAllByAutocreatedIsFalseAndEmployeeIdIn(@Param("employeeIds") Collection<Long> employeeIds);

    List<User> getAllByAutocreatedIsFalseAndResidentIdIn(@Param("residentIds") Collection<Long> residentIds);

    List<User> getAllByAutocreatedIsFalseAndIdIn(@Param("ids") List<Long> userIds);

    Set<User> getByIdIn(List<Long> userIds);

    @Modifying
    @Query("UPDATE User SET timeZoneOffset = :offset WHERE id = :userId")
    void updateTimezone(@Param("userId") Long userId, @Param("offset") Long timeZoneOffset);

    @Modifying
    @Query("UPDATE User SET residentId = :residentId WHERE id = :userId")
    void updateMainResident(@Param("userId") Long userId, @Param("residentId") Long residentId);

}

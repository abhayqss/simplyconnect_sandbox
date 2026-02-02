package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.phr.RegistrationApplication;
import com.scnsoft.eldermark.entity.phr.RegistrationStep;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author phomal
 * <p>
 * Created on 10/17/2017.
 */
@Repository
public interface RegistrationApplicationDao extends JpaRepository<RegistrationApplication, String> {

    @Query("SELECT ra FROM RegistrationApplication ra " +
            "WHERE ra.ssn = :ssn AND ra.emailNormalized = :email AND ra.phoneNormalized = :phone AND ra.firstName LIKE :firstName AND ra.lastName LIKE :lastName " +
            "AND ra.registrationType = :registrationType AND ra.registrationStep NOT IN (:excludedSteps)")
    List<RegistrationApplication> findAllBy(@Param("ssn") String ssn,
                                            @Param("email") String emailNormalized,
                                            @Param("phone") String phoneNormalized,
                                            @Param("firstName") String firstName,
                                            @Param("lastName") String lastName,
                                            @Param("registrationType") RegistrationApplication.Type registrationType,
                                            @Param("excludedSteps") Collection<RegistrationStep> excludedSteps,
                                            Pageable pageable);

    @Query("SELECT ra FROM RegistrationApplication ra " +
            "WHERE ra.emailNormalized = :email AND ra.phoneNormalized = :phone " +
            "AND (:firstName IS NULL OR ra.firstName LIKE :firstName) " +
            "AND (:lastName IS NULL OR ra.lastName LIKE :lastName) " +
            "AND ra.registrationType = :registrationType AND ra.registrationStep NOT IN (:excludedSteps)")
    List<RegistrationApplication> findAllBy(@Param("email") String emailNormalized,
                                            @Param("phone") String phoneNormalized,
                                            @Param("firstName") String firstName,
                                            @Param("lastName") String lastName,
                                            @Param("registrationType") RegistrationApplication.Type registrationType,
                                            @Param("excludedSteps") Collection<RegistrationStep> excludedSteps,
                                            Pageable pageable);

    @Query("SELECT ra FROM RegistrationApplication ra " +
            "WHERE ra.employee = :employee AND ra.emailNormalized = :email AND ra.phoneNormalized = :phone AND ra.firstName LIKE :firstName AND ra.lastName LIKE :lastName " +
            "AND ra.registrationType = :registrationType AND ra.registrationStep NOT IN (:excludedSteps)")
    List<RegistrationApplication> findAllBy(@Param("employee") Employee employee,
                                            @Param("email") String emailNormalized,
                                            @Param("phone") String phoneNormalized,
                                            @Param("firstName") String firstName,
                                            @Param("lastName") String lastName,
                                            @Param("registrationType") RegistrationApplication.Type registrationType,
                                            @Param("excludedSteps") Collection<RegistrationStep> excludedSteps,
                                            Pageable pageable);

    RegistrationApplication findFirstBySsnAndEmailNormalizedAndPhoneNormalizedAndRegistrationStepOrderByCurrentSignupTimeDesc(
            @Param("ssn") String ssn,
            @Param("email") String emailNormalized,
            @Param("phone") String phoneNormalized,
            @Param("step") RegistrationStep step);

    RegistrationApplication findFirstBySsnIsNullAndEmailNormalizedAndPhoneNormalizedAndRegistrationStepOrderByCurrentSignupTimeDesc(
            @Param("email") String emailNormalized,
            @Param("phone") String phoneNormalized,
            @Param("step") RegistrationStep step);

    RegistrationApplication findFirstByEmailNormalizedAndRegistrationStepAndRegistrationTypeOrderByCurrentSignupTimeDesc(
            @Param("email") String email,
            @Param("step") RegistrationStep registrationStep,
            @Param("registrationType") RegistrationApplication.Type registrationType);

    RegistrationApplication findOneByFlowIdAndFirstNameAndRegistrationStepAndRegistrationTypeOrderByCurrentSignupTimeDesc(
            @Param("flowId") String flowId,
            @Param("firstName") String firstName,
            @Param("step") RegistrationStep registrationStep,
            @Param("registrationType") RegistrationApplication.Type registrationType);

}

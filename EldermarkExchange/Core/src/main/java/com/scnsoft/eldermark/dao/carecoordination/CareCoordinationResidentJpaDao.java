package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.shared.carecoordination.careteam.CommunityResidentVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CareCoordinationResidentJpaDao extends JpaRepository<CareCoordinationResident, Long> {

    @Query("Select new com.scnsoft.eldermark.shared.carecoordination.careteam.CommunityResidentVO(res.facility.id, res.id)  from CareCoordinationResident res where res.id in (:residentIds) ")
    List<CommunityResidentVO> getCommunityIdsByResidentsIds(@Param("residentIds") List<Long> residentIds);

    @Query("SELECT r FROM CareCoordinationResident r " +
            "   left join r.database d " +
            "WHERE d.name = :organizationName " +
            "       AND r.firstName = :firstName " +
            "       AND r.lastName = :lastName " +
            "       AND r.birthDate = :dob" +
            "       AND r.active = true" +
            "       AND (r.isOptOut is null or r.isOptOut = false)")
    List<CareCoordinationResident> getAllByIdentityFields(
            @Param("organizationName") String organizationName,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("dob") Date dob);

    @Query("SELECT r FROM CareCoordinationResident r " +
            "   left join r.database d " +
            "WHERE d.name = :organizationName " +
            "       AND r.firstName = :firstName " +
            "       AND r.lastName = :lastName " +
            "       AND r.birthDate = :dob" +
            "       AND r.socialSecurity = :ssn" +
            "       AND r.active = true" +
            "       AND (r.isOptOut is null or r.isOptOut = false)")
    List<CareCoordinationResident> getAllByIdentityFields(
            @Param("organizationName") String organizationName,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("dob") Date dob,
            @Param("ssn") String ssn);


    @Modifying
    @Query("UPDATE CareCoordinationResident set gender = :genderCode where id = :residentId")
    void updateGender(@Param("genderCode") CcdCode genderCode, @Param("residentId") Long residentid);

    @Modifying
    @Query("UPDATE CareCoordinationResident set maritalStatus = :maritalStatusCode where id = :residentId")
    void updateMaritalStatus(@Param("maritalStatusCode") CcdCode maritalStatusCode, @Param("residentId") Long residentid);

    @Query("SELECT r FROM CareCoordinationResident r " +
            "   left join r.mpi mpi " +
            "WHERE mpi.patientId = :mpiPatientId " +
            "       AND mpi.assigningAuthorityUniversal = :assigningAuthorityUniversal" +
            "       AND r.active = true" +
            "       AND (r.isOptOut is null or r.isOptOut = false)")
    List<CareCoordinationResident> findAllActiveByMpiPatientIdAndAssigningAuthorityUniversal(
            @Param("mpiPatientId") String mpiPatientId,
            @Param("assigningAuthorityUniversal") String assigningAuthorityUniversal);

}

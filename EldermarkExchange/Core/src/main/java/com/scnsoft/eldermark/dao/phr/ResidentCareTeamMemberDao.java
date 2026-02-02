package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.ResidentCareTeamMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author phomal
 * Created on 02/01/2018.
 */
@Repository
public interface ResidentCareTeamMemberDao extends JpaRepository<ResidentCareTeamMember, Long> {

    Page<ResidentCareTeamMember> findByResidentId(@Param("residentId") long residentId,
                                                  Pageable pageable);

    Page<ResidentCareTeamMember> findByResidentIdAndCareTeamRoleCode(@Param("residentId") long residentId,
                                                                     @Param("careTeamRoleCode") CareTeamRoleCode roleCode,
                                                                     Pageable pageable);

    Page<ResidentCareTeamMember> findByResidentIdAndCareTeamRoleCodeNot(@Param("residentId") long residentId,
                                                                        @Param("careTeamRoleCode") CareTeamRoleCode roleCode,
                                                                        Pageable pageable);

}

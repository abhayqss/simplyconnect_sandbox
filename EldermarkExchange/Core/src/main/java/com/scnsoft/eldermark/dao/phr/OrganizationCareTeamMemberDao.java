package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.OrganizationCareTeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author phomal
 * Created on 02/01/2018.
 */
@Repository
public interface OrganizationCareTeamMemberDao extends JpaRepository<OrganizationCareTeamMember, Long> {
}

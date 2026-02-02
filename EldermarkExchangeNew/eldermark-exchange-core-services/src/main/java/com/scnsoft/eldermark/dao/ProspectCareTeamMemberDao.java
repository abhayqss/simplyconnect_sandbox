package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.prospect.ProspectCareTeamMember;
import org.springframework.stereotype.Repository;

@Repository
public interface ProspectCareTeamMemberDao extends AppJpaRepository<ProspectCareTeamMember, Long> {
}

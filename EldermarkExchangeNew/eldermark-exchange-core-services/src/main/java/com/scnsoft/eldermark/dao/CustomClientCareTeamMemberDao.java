package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Set;

public interface CustomClientCareTeamMemberDao {

    Map<Boolean, Set<Long>> calculateCurrentOnHoldCandidate(Specification<ClientCareTeamMember> specification);

}

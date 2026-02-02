package com.scnsoft.eldermark.dao.healthpartners;

import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersTermedMember;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthPartnersTermedMemberDao extends JpaRepository<HealthPartnersTermedMember, Long> {

    List<HealthPartnersTermedMember> findAllByHpFileLogId(Long hpFileLogId, Sort sort);
}

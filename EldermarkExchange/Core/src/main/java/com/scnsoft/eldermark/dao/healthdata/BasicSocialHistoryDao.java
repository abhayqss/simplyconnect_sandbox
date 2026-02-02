package com.scnsoft.eldermark.dao.healthdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;

@NoRepositoryBean
public interface BasicSocialHistoryDao <ENTITY> extends JpaRepository<ENTITY, Long> {
    Long countAllBySocialHistory_Resident_IdIn(Collection<Long> residentIds);
}

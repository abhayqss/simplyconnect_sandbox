package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.ResidentAwareAuditableEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;

@NoRepositoryBean
public interface ResidentAwareAuditableEntityDao<ENTITY extends ResidentAwareAuditableEntity> extends JpaRepository<ENTITY, Long>, JpaSpecificationExecutor<ENTITY> {
    Long countByResident_IdInAndArchivedIsFalse(Collection<Long> residentIds);
    Page<ENTITY> getAllByResident_IdInAndArchivedIsFalse(Collection<Long> residentIds, Pageable pageable);

//    Long countByResident_IdInAndArchivedIsFalseAndStatusIsNotDeleted(Collection<Long> residentIds);
//    Page<ENTITY> getAllByResident_IdInAndArchivedIsFalseAndStatusIsNotDeletedOrderByLastModifiedDateDescIdDesc(Collection<Long> residentIds, Pageable pageable);
}

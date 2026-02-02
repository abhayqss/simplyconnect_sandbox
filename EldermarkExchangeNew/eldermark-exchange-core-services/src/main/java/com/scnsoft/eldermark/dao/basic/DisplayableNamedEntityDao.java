package com.scnsoft.eldermark.dao.basic;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.scnsoft.eldermark.entity.basic.DisplayableNamedEntity;

@NoRepositoryBean
public interface DisplayableNamedEntityDao<ENTITY extends DisplayableNamedEntity> extends JpaRepository<ENTITY, Long>{
    
    List<ENTITY> findByOrderByDisplayNameAsc();

    List<ENTITY> findByIdIn(List<Long> ids, Sort by);

}

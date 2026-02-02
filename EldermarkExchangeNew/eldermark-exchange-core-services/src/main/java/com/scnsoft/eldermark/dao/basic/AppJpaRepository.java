package com.scnsoft.eldermark.dao.basic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface AppJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID>,
        JpaSpecificationProjectionExecutor<T>,
        IdProjectionRepository<ID>,
        ExistsBySpecificationRepository<T> {

}

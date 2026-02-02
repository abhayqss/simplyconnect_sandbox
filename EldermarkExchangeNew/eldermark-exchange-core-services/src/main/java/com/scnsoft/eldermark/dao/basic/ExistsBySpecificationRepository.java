package com.scnsoft.eldermark.dao.basic;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ExistsBySpecificationRepository<T> {
    boolean exists(Specification<T> specification);
}

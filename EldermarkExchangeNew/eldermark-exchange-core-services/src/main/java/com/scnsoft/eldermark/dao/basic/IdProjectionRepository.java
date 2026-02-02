package com.scnsoft.eldermark.dao.basic;

import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Actual implementation is {@link AppJpaRepositoryImpl} because in case entity implements projection interface
 * Spring will load full entity from database, whereas our AppJpaRepositoryImpl will still load just needed fields.
 *
 * IMPORTANT! Currently projections with collection fields (like List<smth> field) don't work properly in our implementation (always null)
 */
@NoRepositoryBean
public interface IdProjectionRepository<ID> {

    <T> Optional<T> findById(ID id, Class<T> projection);

    <T> List<T> findByIdIn(Collection<ID> ids, Class<T> projection);

}

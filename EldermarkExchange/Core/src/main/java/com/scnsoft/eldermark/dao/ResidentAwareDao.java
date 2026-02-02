package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.BasicEntity;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * The definition of the Database Access Objects that handle the reading and removal a class from the database
 * by a corresponding {@link com.scnsoft.eldermark.entity.Resident}.
 *
 * @author phomal
 * Created on 2/13/2017.
 */
public interface ResidentAwareDao<T extends BasicEntity> extends BaseDao<T> {

    /**
     * Execute a SELECT query and return the query results as a typed List.
     *
     * @param residentId Resident ID
     * @return a list of the results
     */
    List<T> listByResidentId(Long residentId);

    /**
     * Execute a SELECT query for merged residents and return the aggregated query results as a typed Set.
     *
     * @param residentId Resident ID
     * @return a list of the results
     */
    Collection<T> listByResidentId(Long residentId, boolean aggregated);

    /**
     * Execute a SELECT count(*) query for residents set and return the query result.
     *
     * @param residentIds Resident IDs
     * @return count
     */
    Long countByResidentIds(Collection<Long> residentIds);

    /**
     * Execute a SELECT query for specified residents and return the aggregated query results as a typed Set.
     *
     * @param residents Resident IDs
     * @return a list of the results
     */
    Collection<T> listByResidentIds(List<Long> residents);

    /**
     * Execute a SELECT query for specified residents and return the aggregated query results as a typed Set.
     *
     * @param residents Resident IDs
     * @param pageable Pagination information
     * @return a list of the results
     */
    Collection<T> listByResidentIds(List<Long> residents, Pageable pageable);

    /**
     * Execute delete statement.
     * <br/>
     * Note: this method doesn't support cascade removal, perhaps due to https://hibernate.atlassian.net/browse/HHH-11144
     *
     * @param residentId Resident ID
     * @return the number of entities deleted
     */
    int deleteByResidentId(Long residentId);

}

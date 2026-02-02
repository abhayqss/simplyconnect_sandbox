package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.ResidentLight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author phomal
 * Created on 8/17/2017.
 */
@Repository
public interface ResidentLightDao extends JpaRepository<ResidentLight, Long> {

    @Query("SELECT r FROM ResidentLight r INNER JOIN FETCH r.database WHERE r.id IN (:ids)")
    Set<ResidentLight> findAllByIdIn(@Param("ids") Set<Long> ids);

}

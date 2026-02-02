package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.phr.PhysicianCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author phomal
 * Created on 5/29/2017.
 */
@Repository
public interface PhysicianCategoryDao extends JpaRepository<PhysicianCategory, Long> {

}

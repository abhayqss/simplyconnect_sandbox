package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.document.ccd.Allergy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllergyDao extends AppJpaRepository<Allergy, Long> {

    @Query("select a from Allergy a where a.clientId in (:clientIds)")
    List<Allergy> listByClientIds(@Param("clientIds") List<Long> clientIds);

    void deleteAllByClientId(Long clientId);
}

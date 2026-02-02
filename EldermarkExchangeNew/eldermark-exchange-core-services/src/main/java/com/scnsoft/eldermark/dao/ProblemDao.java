package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.document.ccd.Problem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemDao extends AppJpaRepository<Problem, Long> {

    @Query("SELECT p FROM Problem p  WHERE p.client.id IN (:clientIds)")
    List<Problem> listByClientIds(@Param("clientIds") List<Long> clientIds);

    void deleteAllByClientId(Long clientId);
}


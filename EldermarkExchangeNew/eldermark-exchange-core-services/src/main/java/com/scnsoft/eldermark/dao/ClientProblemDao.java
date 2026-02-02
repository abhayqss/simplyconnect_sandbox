package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.document.ccd.ClientProblem;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientProblemDao extends AppJpaRepository<ClientProblem, Long>,
        CustomClientProblemDao {
}

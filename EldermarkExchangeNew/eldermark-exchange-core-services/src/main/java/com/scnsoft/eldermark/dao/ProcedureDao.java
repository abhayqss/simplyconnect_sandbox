package com.scnsoft.eldermark.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.scnsoft.eldermark.entity.document.ccd.Procedure;

public interface ProcedureDao extends JpaRepository<Procedure, Long> {
    List<Procedure> findByClient_IdIn(List<Long> clientIds);

    void deleteAllByClientId(Long clientId);
}

package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.prospect.history.ProspectHistory;
import org.springframework.stereotype.Repository;

@Repository
public interface ProspectHistoryDao extends AppJpaRepository<ProspectHistory, Long> {
}

package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.MissedChatsAndCalls;
import org.springframework.stereotype.Repository;

@Repository
public interface MissedChatsAndCallsDao extends AppJpaRepository<MissedChatsAndCalls, String> {
}

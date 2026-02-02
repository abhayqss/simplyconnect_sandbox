package com.scnsoft.eldermark.dao.history;

import com.scnsoft.eldermark.dao.CustomClientDao;
import com.scnsoft.eldermark.dao.CustomClientHistoryDao;
import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.history.ClientHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface ClientHistoryDao extends AppJpaRepository<ClientHistory, Long>, CustomClientHistoryDao {

    @Query("select max(ch.modifiedDate) from ClientHistory ch " +
            "where ch.active=:active and ch.clientId=:clientId")
    Optional<Instant> findLatestStatusDateByClientId(@Param("active") boolean active, @Param("clientId") Long clientId);
}

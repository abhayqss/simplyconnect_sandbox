package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IncidentReportDao extends AppJpaRepository<IncidentReport, Long>, CustomIncidentReportDao {

    IncidentReport findByEvent_IdAndArchivedIsFalse(Long eventId);

    IncidentReport findFirstByChainIdAndSubmittedIsTrueOrderById(Long chainId);

    Optional<IdAware> findByEventIdAndArchivedIsFalse(Long eventId);

    @Modifying
    @Query("update IncidentReport ir set ir.twilioConversationSid = :twilioConversationSid where ir.id=:id ")
    void assignConversation(@Param("id") Long id, @Param("twilioConversationSid") String twilioConversationSid);
}

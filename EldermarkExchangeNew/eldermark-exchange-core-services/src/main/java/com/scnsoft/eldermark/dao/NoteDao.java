package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.note.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteDao extends AppJpaRepository<Note, Long>, CustomNoteDao {

    Page<Note> findByIdOrChainId(Long id, Long ChainId, Pageable pageable);

    Page<Note> findAllByEvent_IdAndArchivedIsFalse(Long eventId, Pageable pageable);

    @Query("Select distinct n from Note n join n.noteClients nc left join n.event.eventType et where nc.id in (:clientIds) and archived=false and (et.code <> 'PRU' or et.code is null) order by n.lastModifiedDate desc")
    List<Note> getClientNotesExcludingPatientUpdate(@Param("clientIds") List<Long> clientIds, Pageable pageRequest);

    @Query("Select subType.id from Note n join n.subType subType join n.client client where client.id=:clientId and n.intakeDate is not null and archived=false")
    List<Long> findTakenNoteTypeIdsByClientIdAndIntakeDateIsNotNull(@Param("clientId") Long clientId);

    @Query("Select subType.id from Note n join n.subType subType join n.client client join n.admittanceHistory ah where client.id=:clientId and ah.id = :admittanceHistoryId and archived=false")
    List<Long> findTakenNoteTypeIdsByClientIdAndAdmittanceHistoryId(@Param("clientId") Long clientId, @Param("admittanceHistoryId") Long admittanceHistoryId);

    boolean existsByClient_IdAndSubType_IdAndIntakeDateIsNotNullAndArchivedIsFalse(Long clientId, Long subTypeId);

    boolean existsByClient_IdAndSubType_IdAndIdNotAndIntakeDateIsNotNullAndArchivedIsFalse(Long clientId, Long subTypeId, Long noteId);

    boolean existsByClient_IdAndSubType_IdAndAdmittanceHistory_IdAndArchivedIsFalse(Long clientId, Long subTypeId, Long admittanceHistoryId);

    boolean existsByClient_IdAndSubType_IdAndIdNotAndAdmittanceHistory_IdAndArchivedIsFalse(Long clientId, Long subTypeId, Long noteId, Long admittanceHistoryId);
}

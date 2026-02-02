package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.Note;
import com.scnsoft.eldermark.entity.NoteSubType;
import com.scnsoft.eldermark.entity.NoteType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Repository
public interface NoteDao extends JpaRepository<Note, Long>, JpaSpecificationExecutor<Note> {

    Sort.Order ORDER_BY_LAST_MODIFIED_DATE_DESC = new Sort.Order(DESC, "lastModifiedDate");
    Sort.Order ORDER_BY_LAST_ID_DESC = new Sort.Order(DESC, "id");

    Page<Note> getAllByResident_IdInAndArchivedIsFalseOrderByLastModifiedDateDescIdDesc(Iterable<Long> residentIds, Pageable pageable);

    Page<Note> getAllByResidentIdInAndArchivedIsFalseAndTypeOrResidentIdInAndArchivedIsFalseAndType(Iterable<Long> type1ResidentIds, NoteType type1,
                                                                                                    Iterable<Long> type2ResidentIds, NoteType type2,
                                                                                                    Pageable pageable);

    Page<Note> getAllByResident_IdInAndArchivedIsFalseAndTypeNotOrderByLastModifiedDateDescIdDesc(Iterable<Long> residentIds, NoteType noteType, Pageable pageable);

    Long countByResident_IdInAndArchivedIsFalse(Iterable<Long> residentIds);

    Long countByResident_IdInAndArchivedIsFalseAndTypeNot(Iterable<Long> residentIds, NoteType noteType);

    List<Note> getAllByChainIdOrIdAndArchivedIsTrueOrderByLastModifiedDateDescIdDesc(Long chainId, Long id);

    List<Note> getAllByEventIdAndArchivedIsFalseOrderByLastModifiedDateDescIdDesc(Long eventId);

    Page<Note> getAllByEventIdAndArchivedIsFalseOrderByLastModifiedDateDescIdDesc(Long eventId, Pageable pageable);


    @Query(value = "SELECT (Row-1)/25 FROM (SELECT ROW_NUMBER() OVER (ORDER BY last_modified_date desc, id desc) AS Row, note.id FROM Note note where archived=0 and note.resident_id in (:resident_ids)) us where id= :note_id",
            nativeQuery = true)
    BigInteger getPageNumber(@Param("resident_ids") Iterable<Long> residentIds, @Param("note_id") Long noteId);

    @Query(value = "SELECT COALESCE((SELECT TOP(1) id FROM Note n WHERE n.chain_id = COALESCE(note.chain_id, note.id) ORDER BY last_modified_date DESC, id DESC), id) FROM Note note where id=:noteId", nativeQuery = true)
    Long getLatestForNote(@Param("noteId") Long noteId);

    Long countByResident_IdAndSubTypeAndAdmittanceHistory_IdAndArchivedIsFalse(Long residentId, NoteSubType noteSubType, Long admittanceHistoryId);
    Long countByResident_IdAndSubTypeAndIntakeDateAndArchivedIsFalse(Long residentId, NoteSubType noteSubType, Date intakeDate);

    @Query("Select note from Note note left join note.event.eventType et where note.resident.id in (:residentIds) and archived=0 and (et.code <> 'PRU' or et.code is null) order by note.lastModifiedDate desc")
    List<Note> getTop3ResidentNotesExcludingPatientUpdate(@Param("residentIds") Iterable<Long> residentIds, Pageable pageRequest);
}

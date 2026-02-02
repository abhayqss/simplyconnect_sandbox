package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.dao.projections.NoteAndReadBoolean;
import com.scnsoft.eldermark.entity.phr.NoteReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author phomal
 * Created on 5/17/2017.
 */
@Repository
public interface NoteReadStatusDao extends JpaRepository<NoteReadStatus, Long> {

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN 'true' ELSE 'false' END FROM NoteReadStatus s WHERE s.noteId = :noteId AND s.userId = :userId")
    boolean existsByUserIdAndNoteId(@Param("userId") Long userId, @Param("noteId") Long noteId);

    @Query("SELECT s.noteId AS noteId, CASE WHEN COUNT(s) > 0 THEN 'true' ELSE 'false' END AS read " +
            "FROM NoteReadStatus s WHERE s.noteId IN :noteIds AND s.userId = :userId GROUP BY s.noteId")
    List<NoteAndReadBoolean> getWasReadByUserIdAndNoteIds(@Param("userId") Long userId, @Param("noteIds") Collection<Long> noteIds);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN 'true' ELSE 'false' END AS read " +
            "FROM NoteReadStatus s WHERE s.noteId = :noteId AND s.userId = :userId GROUP BY s.noteId")
    Boolean getWasReadByUserIdAndNoteId(@Param("userId") Long userId, @Param("noteId") Long noteId);

}

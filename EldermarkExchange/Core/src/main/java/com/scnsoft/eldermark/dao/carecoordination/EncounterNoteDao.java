package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.EncounterNote;
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
public interface EncounterNoteDao extends JpaRepository<EncounterNote, Long>, JpaSpecificationExecutor<EncounterNote> {

}

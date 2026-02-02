package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.NoteSubType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NoteSubTypeDao extends JpaRepository<NoteSubType, Long> {

    Sort.Order ORDER_BY_POSITION = new Sort.Order(Sort.Direction.ASC, "position");

    Sort.Order ORDER_BY_DESCRIPTION = new Sort.Order(Sort.Direction.ASC, "description");

}

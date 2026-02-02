package com.scnsoft.eldermark.dao.inbound.document;

import com.scnsoft.eldermark.entity.inbound.document.DocumentAssignmentInputPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentAssignmentInputPathDao extends JpaRepository<DocumentAssignmentInputPath, Long> {

    List<DocumentAssignmentInputPath> findAllByDisabledIsFalse();

}

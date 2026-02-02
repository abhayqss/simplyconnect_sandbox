package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.ClientProblemCount;
import com.scnsoft.eldermark.entity.document.ccd.ClientProblem;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomClientProblemDao {

    List<ClientProblemCount> countGroupedByStatus(Specification<ClientProblem> specification);

}

package com.scnsoft.eldermark.service;

import java.util.List;

import com.scnsoft.eldermark.beans.ClientProblemFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.scnsoft.eldermark.dao.ProblemObservationDao;
import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;

@Service
@Deprecated
@Transactional
public class ProblemServiceImpl implements ProblemService {
    @Autowired
    private ProblemObservationDao problemObservationDao;

    @Autowired
    private ClientService clientService;

    @Override
    public Long count(Long clientId, Boolean active, Boolean resolved, Boolean other) {
        List<Long> mergedClientIds = clientService.findAllMergedClientsIds(clientId);
        return problemObservationDao.countProblemByClientIds(mergedClientIds, active, resolved, other);
    }

    @Override
    public Page<ProblemObservation> find(Long clientId, Boolean active, Boolean resolved, Boolean other,
                                         Pageable pageRequest) {
        List<Long> mergedClientIds = clientService.findAllMergedClientsIds(clientId);
        return problemObservationDao.findProblemByClientIds(mergedClientIds, active, resolved, other, pageRequest);
    }

    @Override
    public ProblemObservation findById(Long problemId) {
        return problemObservationDao.findByProblem_Id(problemId);
    }

}
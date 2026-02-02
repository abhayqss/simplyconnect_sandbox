package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.healthdata.ProblemObservationDao;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.ProblemObservation;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.web.entity.ProblemInfoDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.scnsoft.eldermark.dao.healthdata.ProblemObservationDao.*;

/**
 * @author phomal
 */
@Service
public class ProblemService extends BasePhrService {

    @Autowired
    private Converter<ProblemObservation, ProblemInfoDto> problemInfoDtoConverter;

    final ProblemObservationDao problemObservationDao;
    private final CareTeamSecurityUtils careTeamSecurityUtils;

    @Autowired
    public ProblemService(CareTeamSecurityUtils careTeamSecurityUtils, ProblemObservationDao problemObservationDao) {
        this.careTeamSecurityUtils = careTeamSecurityUtils;
        this.problemObservationDao = problemObservationDao;
    }

    @Transactional(readOnly = true)
    public Page<ProblemInfoDto> getUserProblemsActive(Long userId, Pageable pageable) {
        return getUserProblems(userId, true, false, false, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProblemInfoDto> getUserProblemsResolved(Long userId, Pageable pageable) {
        return getUserProblems(userId, false, true, false, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProblemInfoDto> getUserProblemsOther(Long userId, Pageable pageable) {
        return getUserProblems(userId, false, false, true, pageable);
    }

    private Page<ProblemInfoDto> getUserProblems(Long userId, boolean includeActive, boolean includeResolved, boolean includeOther, Pageable pageable) {

        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);

        final Sort sort = new Sort(ORDER_BY_START_DATE_DESC, ORDER_BY_NAME);
        final Pageable pageableWithSort;
        if (pageable == null) {
            pageableWithSort = new PageRequest(0, Integer.MAX_VALUE, sort);
        } else {
            pageableWithSort = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }

        Collection<Long> activeResidentIds = getResidentIdsOrThrow(userId);
        Page<ProblemObservation> page = problemObservationDao.listResidentProblemsWithoutDuplicates(activeResidentIds, includeActive, includeResolved, includeOther, pageableWithSort);
        final Page<ProblemInfoDto> resultingPage = page.map(problemInfoDtoConverter);

        return resultingPage;
    }

    @Transactional(readOnly = true)
    public ProblemInfoDto getUserProblem(Long userId, Long problemObservationId) {

        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);

        ProblemObservation src = problemObservationDao.getOne(problemObservationId);
        if (src == null) {
            throw new PhrException(PhrExceptionType.PROBLEM_NOT_FOUND);
        }

        // validate association
        Collection<Long> activeResidentIds = getResidentIdsOrThrow(userId);
        if (!activeResidentIds.contains(src.getProblem().getResidentId())) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        return problemInfoDtoConverter.convert(src);
    }

    public Converter<ProblemObservation, ProblemInfoDto> getProblemInfoDtoConverter() {
        return problemInfoDtoConverter;
    }

    public void setProblemInfoDtoConverter(Converter<ProblemObservation, ProblemInfoDto> problemInfoDtoConverter) {
        this.problemInfoDtoConverter = problemInfoDtoConverter;
    }
}

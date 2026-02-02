package com.scnsoft.eldermark.services.ccd.section;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.CcdCodeDao;
import com.scnsoft.eldermark.dao.ProblemDao;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.dao.exceptions.NotUniqueValueException;
import com.scnsoft.eldermark.dao.healthdata.ProblemObservationDao;
import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.entity.Problem;
import com.scnsoft.eldermark.entity.ProblemObservation;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.facades.carecoordination.PatientFacade;
import com.scnsoft.eldermark.services.carecoordination.CareCoordinationConstants;
import com.scnsoft.eldermark.services.carecoordination.EventService;
import com.scnsoft.eldermark.services.carecoordination.EventTypeService;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.shared.carecoordination.EmployeeDto;
import com.scnsoft.eldermark.shared.carecoordination.EventDetailsDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import com.scnsoft.eldermark.shared.ccd.CcdCodeDto;
import com.scnsoft.eldermark.shared.ccd.ProblemObservationDto;
import com.scnsoft.eldermark.shared.exceptions.BusinessAccessDeniedException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class ProblemServiceImpl implements ProblemService {
    private static final Logger logger = LoggerFactory.getLogger(ProblemServiceImpl.class);

    private static final List<String> CODE_SYSTEMS = Collections.singletonList(ICD_10_CM_CODE_SYSTEM_CODE);

    private static final String INACTIVE_STATUS_CODE = "73425007";
    private static final String RESOLVED_STATUS_CODE = "413322009";

    private static final List<String> INACTIVE_STATUS_CODES = Arrays.asList(INACTIVE_STATUS_CODE, RESOLVED_STATUS_CODE);

    @Autowired
    private CcdCodeDao ccdCodeDao;

    @Autowired
    private ProblemObservationDao problemObservationDao;

    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private ResidentDao residentDao;

    @Autowired
    private EventService eventService;

    @Autowired
    private PatientFacade patientFacade;

    @Autowired
    private EventTypeService eventTypeService;

    @Override
    @Transactional(readOnly = true)
    public Page<CcdCodeDto> listDiagnosisCodes(String searchCriterion, Pageable pageRequest) {
        List<CcdCode> ccdCodeList = ccdCodeDao.listByCodeOrDisplayName(searchCriterion,
                CODE_SYSTEMS,
                pageRequest.getOffset(),
                pageRequest.getPageSize());
        final Long total = ccdCodeDao.countByCodeOrDisplayName(searchCriterion,
                CODE_SYSTEMS);
        return new PageImpl<>(CcdUtils.transform(ccdCodeList), pageRequest, total);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CcdCodeDto> listDiagnosisCodesWithSameName(Long diagnosisCodeId) {
        return CcdUtils.transform(ccdCodeDao.listWithSameDisplayName(diagnosisCodeId,
                CODE_SYSTEMS));
    }

    @Override
    public void createProblemObservation(ProblemObservationDto problemObservationDto, Long residentId) {
        final Resident resident = residentDao.get(residentId);

        if (!checkProblemIsUnique(problemObservationDto, residentId)) {
            logger.warn("Attempt to insert exixting problem: residentId = {}, problem code = {}");
            throw new NotUniqueValueException("Such a problem already exists in the system");
        }
        //create Problem for this observation
        Problem problem = new Problem();
        if (problemObservationDto.getEndDate() != null && new Date().before(problemObservationDto.getEndDate())) {
            problem.setStatusCode("active");
        } else {
            problem.setStatusCode("completed");
        }
        problem.setTimeLow(problemObservationDto.getStartDate());
        problem.setTimeHigh(problemObservationDto.getEndDate());
        problem.setResident(resident);

        CareCoordinationConstants.setLegacyId(problem);

        problem.setDatabase(resident.getDatabase());
        problem = problemDao.create(problem);

        //create ProblemObservation itself
        ProblemObservation problemObservation = new ProblemObservation();
        problemObservation.setNegationInd(Boolean.FALSE);

        putProblemValue(problemObservation, problemObservationDto);
        putProblemType(problemObservation, problemObservationDto);
        putProblemStatus(problemObservation, problemObservationDto);


        problemObservation.setProblemDateTimeLow(problemObservationDto.getStartDate());
        problemObservation.setProblemDateTimeHigh(problemObservationDto.getEndDate());

        putOnSetDate(problemObservation, problemObservationDto, resident);


        problemObservation.setProblem(problem);

        setPrimaryObservation(problemObservation, residentId, problemObservationDto.getPrimary());

        problemObservation.setRecordedDate(problemObservationDto.getRecordedDate());

        problemObservation.setRecordedBy(SecurityUtils.getAuthenticatedUser().getEmployee());
        problemObservation.setComments(problemObservationDto.getComments());

        problemObservation.setManual(true);

        CareCoordinationConstants.setLegacyId(problemObservation);
        problemObservation.setDatabase(resident.getDatabase());
        problemObservation.setDatabaseId(resident.getDatabase().getId());

        problemObservation = problemObservationDao.saveAndFlush(problemObservation);

        eventService.processAutomaticEvent(createEventDto(problemObservation));
    }

    private boolean checkProblemIsUnique(ProblemObservationDto problemObservationDto, Long residentId) {
        final CcdCode problemCode = ccdCodeDao.find(problemObservationDto.getValue().getId());
        return !problemObservationDao.getTopByProblemNameAndProblemTypeIdAndProblemResidentId(problemCode.getDisplayName(),
                problemObservationDto.getType().getId(),
                //, problemObservationDto.getRecordedDate(),
                residentId).isPresent();
    }

    private void setPrimaryObservation(ProblemObservation problemObservation, long residentId, Boolean primary) {
        if (BooleanUtils.isNotTrue(problemObservation.getPrimary()) && BooleanUtils.isTrue(primary)) {
            final Optional<ProblemObservation> oldPrimary = problemObservationDao.getByProblemResidentIdAndPrimaryIsTrue(residentId);
            if (oldPrimary.isPresent()) {
                oldPrimary.get().setPrimary(false);
                problemObservationDao.saveAndFlush(oldPrimary.get());
            }
        }
        problemObservation.setPrimary(primary);
    }

    private void putOnSetDate(ProblemObservation problemObservation, ProblemObservationDto problemObservationDto, Resident resident) {
        if (resident.getBirthDate() != null && problemObservationDto.getOnSetDate() != null) {
            problemObservation.setOnsetDate(problemObservationDto.getOnSetDate());
            long diffInMillis = Math.abs(problemObservationDto.getOnSetDate().getTime() - resident.getBirthDate().getTime());
            problemObservation.setAgeObservationValue((int) TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS));
            problemObservation.setAgeObservationUnit("day");
        }
    }

    @Override
    public void editProblemObservation(ProblemObservationDto problemObservationDto, Long residentId) {
        final ProblemObservation problemObservation = problemObservationDao.getOne(problemObservationDto.getId());

        if (!problemObservation.getManual()) {
            throw new BusinessAccessDeniedException("Only problems added via Simply Connect System can be edited.");
        }

        putProblemValue(problemObservation, problemObservationDto);
        setPrimaryObservation(problemObservation, problemObservation.getProblem().getResidentId(), problemObservationDto.getPrimary());
        putProblemType(problemObservation, problemObservationDto);
        putProblemStatus(problemObservation, problemObservationDto);

        problemObservation.setProblemDateTimeLow(problemObservationDto.getStartDate());
        problemObservation.setProblemDateTimeHigh(problemObservationDto.getEndDate());

        putOnSetDate(problemObservation, problemObservationDto, problemObservation.getProblem().getResident());

        problemObservation.setComments(problemObservationDto.getComments());
        problemObservationDao.saveAndFlush(problemObservation);
    }

    private void putProblemValue(ProblemObservation problemObservation, ProblemObservationDto problemObservationDto) {
        final CcdCode problemCode = ccdCodeDao.find(problemObservationDto.getValue().getId());
        problemObservation.setProblemCode(problemCode);
        problemObservation.setProblemName(problemCode.getDisplayName());
        problemObservation.setProblemIcdCode(problemCode.getCode());
        problemObservation.setProblemIcdCodeSet(problemCode.getCodeSystemName());
    }

    private void putProblemType(ProblemObservation problemObservation, ProblemObservationDto problemObservationDto) {
        final CcdCode problemType = ccdCodeDao.find(problemObservationDto.getType().getId());
        problemObservation.setProblemType(problemType);
    }

    private void putProblemStatus(ProblemObservation problemObservation, ProblemObservationDto problemObservationDto) {
        final CcdCode problemStatus = ccdCodeDao.find(problemObservationDto.getStatus().getId());
        problemObservation.setProblemStatusCode(problemStatus);
        problemObservation.setProblemStatusText(problemStatus.getDisplayName());
    }

    private EventDto createEventDto(ProblemObservation problemObservation) {
        final EventDto eventDto = new EventDto();

        eventDto.setPatient(patientFacade.getPatientDto(problemObservation.getProblem().getResident().getId(),
                false, false));

        eventDto.setEmployee(new EmployeeDto());
        eventDto.getEmployee().setRoleId(problemObservation.getRecordedBy().getCareTeamRole().getId());
        eventDto.getEmployee().setFirstName(problemObservation.getRecordedBy().getFirstName());
        eventDto.getEmployee().setLastName(problemObservation.getRecordedBy().getLastName());

        final EventDetailsDto eventDetailsDto = new EventDetailsDto();
        eventDetailsDto.setEventDatetime(new Date());
        eventDetailsDto.setSituation("\"Problems\" CCD data have been added to the patient record.");
        eventDetailsDto.setEventTypeId(eventTypeService.getByCode("GENERAL").getId());
        eventDto.setEventDetails(eventDetailsDto);
        return eventDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemObservationDto getProblemObservationDto(Long problemObservationId) {
        final ProblemObservationDto problemObservationDto = new ProblemObservationDto();
        final ProblemObservation problemObservation = problemObservationDao.findOne(problemObservationId);
        problemObservationDto.setId(problemObservation.getId());
        if (problemObservation.getProblemCode() == null) {
            if (CollectionUtils.isNotEmpty(problemObservation.getTranslations())) {
                problemObservationDto.setValue(CcdUtils.transform(problemObservation.getTranslations().iterator().next()));
            } else {
                CcdCodeDto codeDto = new CcdCodeDto();
                codeDto.setDisplayName(problemObservation.getProblemName());
                codeDto.setCodeSystemName(problemObservation.getProblemIcdCodeSet());
                codeDto.setCode(problemObservation.getProblemIcdCode());

                //workaround for life-search dropdown, to be fixed
                codeDto.setId(-1L);
                //

                problemObservationDto.setValue(codeDto);
            }
        }
        else{
            problemObservationDto.setValue(CcdUtils.transform(problemObservation.getProblemCode()));
        }
        problemObservationDto.setPrimary(problemObservation.getPrimary());
        problemObservationDto.setType(CcdUtils.transform(problemObservation.getProblemType()));

        CcdCode status = problemObservation.getProblemStatusCode();
        problemObservationDto.setStatus(CcdUtils.transform(status));

        if (status != null && INACTIVE_STATUS_CODES.contains(status.getCode())) {
            problemObservationDto.setEndDate(problemObservation.getProblemDateTimeHigh());
        }

        problemObservationDto.setStartDate(problemObservation.getProblemDateTimeLow());
        problemObservationDto.setOnSetDate(problemObservation.getOnsetDate());
        problemObservationDto.setRecordedDate(problemObservation.getRecordedDate());
        if (problemObservation.getRecordedBy() != null) {
            problemObservationDto.setRecordedBy(problemObservation.getRecordedBy().getFullName());
        }

        if (StringUtils.isNotEmpty(problemObservation.getHealthStatusObservationText())) {
            problemObservationDto.setHealthStatusObservation(problemObservation.getHealthStatusObservationText());
        } else if (problemObservation.getHealthStatusCode() != null) {
            problemObservationDto.setHealthStatusObservation(problemObservation.getHealthStatusCode().getDisplayName());
        }

        if (problemObservation.getManual()) {
            problemObservationDto.setDataSource("Simply Connect HIE");
        } else if (problemObservation.getDatabase() != null) {
            problemObservationDto.setDataSource(problemObservation.getDatabase().getName());
        }

        problemObservationDto.setComments(problemObservation.getComments());
        return problemObservationDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Long> getPrimaryObservationId(Long residentId) {
        return problemObservationDao.getByProblemResidentIdAndPrimaryIsTrue(residentId)
                .transform(new Function<ProblemObservation, Long>() {
                    @Override
                    public Long apply(ProblemObservation observation) {
                        return observation.getId();
                    }
                });
    }

    @Override
    public void deleteProblemObservation(Long problemObservationId) {
        final ProblemObservation problemObservation = problemObservationDao.getOne(problemObservationId);
        if (!problemObservation.getManual()) {
            throw new BusinessAccessDeniedException("Can not delete problems which are not created manually");
        }
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

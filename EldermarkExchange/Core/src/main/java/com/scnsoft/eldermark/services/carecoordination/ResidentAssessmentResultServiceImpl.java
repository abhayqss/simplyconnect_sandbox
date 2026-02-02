  package com.scnsoft.eldermark.services.carecoordination;

  import com.fasterxml.jackson.databind.ObjectMapper;
  import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
  import com.scnsoft.eldermark.authentication.SecurityUtils;
  import com.scnsoft.eldermark.converter.assessment.ComprehensiveAssessment;
  import com.scnsoft.eldermark.dao.CcdCodeDao;
  import com.scnsoft.eldermark.dao.ResidentDao;
  import com.scnsoft.eldermark.dao.carecoordination.*;
  import com.scnsoft.eldermark.entity.*;
  import com.scnsoft.eldermark.facades.beans.ComprehensiveAssessmentBean;
  import com.scnsoft.eldermark.services.merging.MPIService;
  import com.scnsoft.eldermark.shared.carecoordination.assessments.*;
  import com.scnsoft.eldermark.shared.exceptions.AssessmentDownloadException;
  import org.apache.commons.collections.CollectionUtils;
  import org.apache.commons.lang3.BooleanUtils;
  import org.apache.commons.lang3.StringUtils;
  import org.json.simple.JSONArray;
  import org.json.simple.JSONObject;
  import org.json.simple.parser.JSONParser;
  import org.json.simple.parser.ParseException;
  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.data.domain.Page;
  import org.springframework.data.domain.PageImpl;
  import org.springframework.data.domain.Pageable;
  import org.springframework.security.core.GrantedAuthority;
  import org.springframework.stereotype.Service;

  import javax.servlet.http.Cookie;
  import javax.servlet.http.HttpServletResponse;
  import java.io.IOException;
  import java.text.SimpleDateFormat;
  import java.util.*;

  import static java.lang.Boolean.TRUE;

  @Service
  public class ResidentAssessmentResultServiceImpl extends AuditableEntityServiceImpl<ResidentAssessmentResult, ResidentAssessmentResultDto> implements ResidentAssessmentResultService {

      private static final Logger logger = LoggerFactory.getLogger(ResidentAssessmentResultServiceImpl.class);
      private static final Integer ABNORMAL_STATE = 5;
      private static final String SLUMS_ASSESSMENT_TYPE = "SLUMS";
      private static final String COMPREHENSIVE_ASSESSMENT_CODE = "COMPREHENSIVE";

      private static final ObjectMapper objectMapper = new ObjectMapper();

      @Autowired
      private ResidentAssessmentResultDao residentAssessmentResultDao;

      @Autowired
      private AssessmentDao assessmentDao;

      @Autowired
      private ResidentDao residentDao;

      @Autowired
      private CareCoordinationResidentJpaDao careCoordinationResidentJpaDao;

      @Autowired
      private MPIService mpiService;

      @Autowired
      private AssessmentScoringValueDao assessmentScoringValueDao;

      @Autowired
      private AssessmentScoringGroupDao assessmentScoringGroupDao;

      @Autowired
      private ResidentAssessmentResultNotificationService residentAssessmentResultNotificationService;

      @Override
      protected ResidentAssessmentResult save(ResidentAssessmentResult entity) {
          final ResidentAssessmentResult residentAssessmentResult = residentAssessmentResultDao.saveAndFlush(entity);
          return residentAssessmentResult;
      }

      @Override
      protected ResidentAssessmentResult dtoToEntity(ResidentAssessmentResultDto residentAssessmentResultDto) {
          ResidentAssessmentResult residentAssessmentResult = new ResidentAssessmentResult();
          residentAssessmentResult.setAssessment(assessmentDao.getOne(residentAssessmentResultDto.getAssessmentId()));
          residentAssessmentResult.setChainId(residentAssessmentResultDto.getChainId());
          residentAssessmentResult.setComment(residentAssessmentResultDto.getComment());
          residentAssessmentResult.setDateAssigned(residentAssessmentResultDto.getDateAssigned() != null
                  ? residentAssessmentResultDto.getDateAssigned() : residentAssessmentResultDto.getDateCompleted());
          residentAssessmentResult.setDateCompleted(residentAssessmentResultDto.getDateCompleted());
          residentAssessmentResult.setEmployee(SecurityUtils.getAuthenticatedUser().getEmployee());
          residentAssessmentResult.setResident(residentDao.getResident(residentAssessmentResultDto.getPatientId()));
          residentAssessmentResult.setResult(residentAssessmentResultDto.getResultJson());
          residentAssessmentResult.setId(residentAssessmentResultDto.getId());
          residentAssessmentResult.setAssessment_status(residentAssessmentResultDto.getAssessment_status());
          return residentAssessmentResult;
      }

      @Override
      protected void postCreate(ResidentAssessmentResult entity, final ResidentAssessmentResultDto residentAssessmentResultDto) {
          Assessment assessment = entity.getAssessment();
          if (assessment != null && !TRUE.equals(assessment.getShouldSendEvents())) {
              return;
          }
          if (residentAssessmentResultDto.getId() != null) {
              ResidentAssessmentResult previousResidentAssessmentResult = residentAssessmentResultDao.findOne(residentAssessmentResultDto.getId());
              Long prevResult = previousResidentAssessmentResult.getAssessment().getScoringEnabled() ?
                      calculateAssessmentScore(assessmentDao.findOne(residentAssessmentResultDto.getAssessmentId()), previousResidentAssessmentResult.getResult())
                      : 0;
              if (previousResidentAssessmentResult.getEvent() != null
                      || (residentAssessmentResultDto.getScore() != null && prevResult >= ABNORMAL_STATE)) {
                  residentAssessmentResultNotificationService.createNoteForResidentAssessmentResultEvent(entity.getLastModifiedDate(),
                          previousResidentAssessmentResult,
                          calculateAssessmentScoringResults(residentAssessmentResultDto.getAssessmentId(), residentAssessmentResultDto.getResultJson()));
              }
          }
          //TODO set score to send notification in Assessment entity
          if (residentAssessmentResultDto.getScore() != null && residentAssessmentResultDto.getScore() >= ABNORMAL_STATE) {
              Event event = residentAssessmentResultNotificationService.sendAssessmentNotifications(entity, residentAssessmentResultDto.getScore());
              entity.setEvent(event);
              residentAssessmentResultDao.saveAndFlush(entity);
          }
      }

      @Override
      protected ResidentAssessmentResult findById(Long id) {
          return residentAssessmentResultDao.findOne(id);
      }

      /*@Override
      public Long createAssessmentResult(ResidentAssessmentResultDto residentAssessmentResultDto) {
          final ResidentAssessmentResult residentAssessmentResult = residentAssessmentResultDao.saveAndFlush(fromResidentAssessmentResultDto(residentAssessmentResultDto));
          //TODO set score to send notification in Assessment entity
          if (residentAssessmentResultDto.getScore() != null && residentAssessmentResultDto.getScore() >= 5) {
              residentAssessmentResultNotificationService.sendAssessmentNotifications(residentAssessmentResult, residentAssessmentResultDto.getScore());
          }
          return residentAssessmentResult.getId();
      }*/

      @Override
      public Long count(Long patientId) {
          if (SecurityUtils.isAffiliatedView()) {
              return residentAssessmentResultDao.countResidentAssessmentsForAffiliated(getMatchedResidentIds(patientId));
          } else {
              return residentAssessmentResultDao.countByResident_IdInAndArchivedIsFalse(getMatchedResidentIds(patientId));
          }
      }

      @Override
      public Page<ResidentAssessmentResultListDto> list(Long patientId, AssessmentsFilterDto assessmentsFilterDto, Pageable pageRequest) {
          Boolean isAffiliated = SecurityUtils.isAffiliatedView();
          Page<ResidentAssessmentResult> result;
          if (assessmentsFilterDto != null && StringUtils.isNotBlank(assessmentsFilterDto.getName())) {
              String[] searchText = assessmentsFilterDto.getName().split("\\s+");
              String firstSearchPart = "%" + searchText[0] + "%";
              String secondSearchPart = searchText.length != 1 ? "%" + searchText[searchText.length - 1] + "%" : "";
              result = getAllByResidentIdInAndArchivedIsFalseWithSearch(getMatchedResidentIds(patientId), firstSearchPart, secondSearchPart, isAffiliated, pageRequest);
          } else {
              result = getAllByResidentIdInAndArchivedIsFalse(getMatchedResidentIds(patientId), isAffiliated, pageRequest);
          }
          return new PageImpl<>(toAssessmentListDto(result.getContent()), pageRequest, result.getTotalElements());
      }

      private Page<ResidentAssessmentResult> getAllByResidentIdInAndArchivedIsFalseWithSearch(Collection<Long> residentIds, String firstSearchPart, String secondSearchPart, Boolean isAffiliated, Pageable pageable) {
          if (isAffiliated) {
              return residentAssessmentResultDao.getAllByResident_IdInAndArchivedIsFalseWithSearchForAffiliated(residentIds, firstSearchPart, secondSearchPart, pageable);
          } else {
              return residentAssessmentResultDao.getAllByResident_IdInAndArchivedIsFalseWithSearch(residentIds, firstSearchPart, secondSearchPart, pageable);
          }
      }

      private Page<ResidentAssessmentResult> getAllByResidentIdInAndArchivedIsFalse(Collection<Long> residentIds, Boolean isAffiliated, Pageable pageable) {
          if (isAffiliated) {
              return residentAssessmentResultDao.getAllByResident_IdInAndArchivedIsFalseForAffiliated(residentIds, pageable);
          } else {
              return residentAssessmentResultDao.getAllByResident_IdInAndArchivedIsFalse(residentIds, pageable);
          }
      }

      @Override
      public ResidentAssessmentScoringDto calculateAssessmentScoringResults(Long assessmentId, String resultJson) {
          Assessment assessment = assessmentDao.findOne(assessmentId);
          ResidentAssessmentScoringDto residentAssessmentScoringDto = new ResidentAssessmentScoringDto();
          Long score = assessment.getScoringEnabled() ? calculateAssessmentScore(assessment, resultJson) : 0;
          AssessmentScoringGroup assessmentScoringGroup = findAssessmentScoringGroup(assessment, score, resultJson);
          residentAssessmentScoringDto.setAssessmentId(assessmentId);
          residentAssessmentScoringDto.setAssessmentScore(score);
          residentAssessmentScoringDto.setAssessmentShortName(assessment.getShortName());
          residentAssessmentScoringDto.setAssessmentName(assessment.getName());
          residentAssessmentScoringDto.setScoringEnabled(assessment.getScoringEnabled());
          residentAssessmentScoringDto.setType(assessment.getType());
          residentAssessmentScoringDto.setShouldSendEvents(assessment.getShouldSendEvents());
          if (assessment.getScoringEnabled() != null && assessment.getScoringEnabled()) {
              residentAssessmentScoringDto.setComment(assessmentScoringGroup.getComments());
              residentAssessmentScoringDto.setWarning(assessmentScoringGroup.getHighlighting());
              residentAssessmentScoringDto.setSeverity(assessmentScoringGroup.getSeverity());
              residentAssessmentScoringDto.setManagementComment(assessment.getManagementComment());
              residentAssessmentScoringDto.setSeverityColumnName(assessment.getSeverityColumnName());
              residentAssessmentScoringDto.setSeverityShort(assessmentScoringGroup.getSeverityShort());
          }
          return residentAssessmentScoringDto;
      }

      private AssessmentScoringGroup findAssessmentScoringGroup(Assessment assessment, Long score, String resultJson) {
          AssessmentScoringGroup assessmentScoringGroup;
          switch (assessment.getShortName()) {
              case SLUMS_ASSESSMENT_TYPE:
                  boolean hasPassedHighSchool = hasPatientPassedHighSchoolEducation(resultJson);
                  assessmentScoringGroup = assessmentScoringGroupDao.findFirstByAssessment_IdAndScoreLowLessThanEqualAndScoreHighGreaterThanEqualAndPassedHighEducation(assessment.getId(), score, score, hasPassedHighSchool);
                  break;
              default:
                  assessmentScoringGroup = assessmentScoringGroupDao.findFirstByAssessment_IdAndScoreLowLessThanEqualAndScoreHighGreaterThanEqual(assessment.getId(), score, score);
                  break;
          }
          return assessmentScoringGroup;
      }

      private boolean hasPatientPassedHighSchoolEducation(String resultJson) {
          Map<String, List<String>> questionAnswerMap = parseSurveyResult(resultJson);
          List<String> answerList = questionAnswerMap.get("question1");
          return answerList.contains("item1");
      }

      @Override
      public ResidentAssessmentScoringDto calculateAssessmentScoringResults(Long residentAssessmentResultId) {
          ResidentAssessmentResult residentAssessmentResult = residentAssessmentResultDao.findOne(residentAssessmentResultId);
          ResidentAssessmentScoringDto result = calculateAssessmentScoringResults(residentAssessmentResult.getAssessment().getId(), residentAssessmentResult.getResult());
          result.setId(residentAssessmentResultId);
          result.setCompletedBy(residentAssessmentResult.getEmployee().getFullName() + ", " + residentAssessmentResult.getEmployee().getCareTeamRole().getDisplayName());
          result.setDateCompleted(residentAssessmentResult.getDateCompleted());
          result.setComment(residentAssessmentResult.getComment());
          result.setAssessmentResult(residentAssessmentResult.getResult());
          result.setAssessmentContent(residentAssessmentResult.getAssessment().getContent());
          result.setHasNumeration(residentAssessmentResult.getAssessment().getHasNumeration());
          return result;
      }

      @Override
      public ResidentAssessmentResultDto find(Long residentAssessmentResultId) {
          return entityToDto(residentAssessmentResultDao.findOne(residentAssessmentResultId));
      }

      @Override
      public ResidentAssessmentPriorityCheckDto showPriorityCheckResult(ResidentAssessmentResultDto residentAssessmentResultDto) {
          Assessment assessment = assessmentDao.getOne(residentAssessmentResultDto.getAssessmentId());
          ResidentAssessmentPriorityCheckDto residentAssessmentPriorityCheckDto = new ResidentAssessmentPriorityCheckDto();
          residentAssessmentPriorityCheckDto.setAssessmentContent(residentAssessmentResultDto.getResultJson());
          residentAssessmentPriorityCheckDto.setHasNumeration(assessment.getHasNumeration());
          residentAssessmentPriorityCheckDto.setAssessmentName(assessment.getName());
          return residentAssessmentPriorityCheckDto;
      }

      @Override
      public void downloadResidentAssessmentResult(Long assessmentResultId, HttpServletResponse response, int timezoneOffset) {
          //todo access check
          ResidentAssessmentResult assessmentResult = residentAssessmentResultDao.findOne(assessmentResultId);

          ResidentAssessmentResultDownloadDto downloadDto = new ResidentAssessmentResultDownloadDto(
                  assessmentResult.getResident().getId(),
                  assessmentResult.getResult()
          );

          String filename = buildDownloadFileName(assessmentResult, timezoneOffset);

          copyDocumentContentToResponse(downloadDto, filename, response);

      }

      @Override
      public List<ComprehensiveAssessmentBean> parseComprehensiveAssessments(Long residentId) {
          List<ResidentAssessmentResult> residentAssessmentResultList = residentAssessmentResultDao.getComprehensiveAssessmentsOfResident(residentId, COMPREHENSIVE_ASSESSMENT_CODE);
          List<ComprehensiveAssessmentBean> comprehensiveAssessmentBeanList = new ArrayList<>();
          if (CollectionUtils.isNotEmpty(residentAssessmentResultList)){
              for (ResidentAssessmentResult ras : residentAssessmentResultList){
                  try {
                      ComprehensiveAssessmentBean comprehensiveAssessmentBean = objectMapper.readValue(ras.getResult(), ComprehensiveAssessmentBean.class);
                      comprehensiveAssessmentBeanList.add(comprehensiveAssessmentBean);
                  } catch (IOException ex) {
                      logger.error("Error while parsing comprehensive assessment for resident # {}", residentId, ex);
                      return null;
                  }
              }
          }
          return comprehensiveAssessmentBeanList;
      }

      private String buildDownloadFileName(ResidentAssessmentResult assessmentResult, int timezoneOffset) {
          Resident resident = assessmentResult.getResident();

          String initials = StringUtils.join(StringUtils.left(resident.getFirstName(), 1), StringUtils.left(resident.getLastName(),1));

          String assessmentType = assessmentResult.getAssessment().getShortName().toLowerCase();

          Date shiftedAssignedDate = new Date(assessmentResult.getDateAssigned().getTime() - timezoneOffset);
          String dateStr = new SimpleDateFormat("MM-dd-yyyy").format(shiftedAssignedDate);

          return initials + " " + assessmentType + " " + dateStr + ".txt";

      }

      private void copyDocumentContentToResponse(ResidentAssessmentResultDownloadDto document, String fileName, HttpServletResponse response) {
          String contentType = "text/plain";
          String openType = "attachment";

          response.setContentType(contentType);
          response.setHeader("Content-Disposition", openType + ";filename=\"" + fileName + "\"");

          Cookie cookie = new Cookie("fileDownload", "true");
          cookie.setPath("/");
          response.addCookie(cookie); //for jQuery file download plugin

          try {
              objectMapper.writerFor(ResidentAssessmentResultDownloadDto.class).writeValue(response.getOutputStream(), document);
          } catch (IOException e) {
              throw new AssessmentDownloadException();
          }
      }

      ResidentAssessmentResultDto entityToDto(ResidentAssessmentResult residentAssessmentResult) {
          ResidentAssessmentResultDto dto = new ResidentAssessmentResultDto();
          dto.setResultJson(residentAssessmentResult.getResult());
          dto.setChainId(residentAssessmentResult.getChainId());
          dto.setAssessmentId(residentAssessmentResult.getAssessment().getId());
          dto.setEmployeeId(residentAssessmentResult.getEmployee().getId());
          dto.setEmployeeName(residentAssessmentResult.getEmployee().getFullName());
          dto.setPatientId(residentAssessmentResult.getResident().getId());
          dto.setComment(residentAssessmentResult.getComment());
          dto.setDateAssigned(residentAssessmentResult.getDateAssigned() == null ? residentAssessmentResult.getDateCompleted() : residentAssessmentResult.getDateAssigned());
          dto.setDateCompleted(residentAssessmentResult.getDateCompleted());
          dto.setComment(residentAssessmentResult.getComment());
          dto.setId(residentAssessmentResult.getId());
          dto.setAssessment_status(residentAssessmentResult.getAssessment_status());
          return dto;
      }

      private Long calculateAssessmentScore(Assessment assessment, String resultJson) {
          Long assessmentScore = 0l;
          Map<String, List<String>> questionAnswerMap = parseSurveyResult(resultJson);
          Set<String> questionsSet = questionAnswerMap.keySet();
          List<AssessmentScoringValue> assessmentScoringValues = assessmentScoringValueDao.getAllByAssessment(assessment);
          for (String question : questionsSet) {
              List<String> answerList = questionAnswerMap.get(question);
              for (String answer : answerList) {
                  AssessmentScoringValue assessmentScoringValue = findScoringValue(assessmentScoringValues, question, answer);
                  if (assessmentScoringValue != null) {
                      assessmentScore += assessmentScoringValue.getValue();
                  }
              }
          }
          return assessmentScore;
      }

      private AssessmentScoringValue findScoringValue(List<AssessmentScoringValue> assessmentScoringValues, String question, String answer) {
          for (AssessmentScoringValue assessmentScoringValue : assessmentScoringValues) {
              if (assessmentScoringValue.getAnswerName().equalsIgnoreCase(answer) &&
                      (assessmentScoringValue.getQuestionName() == null || assessmentScoringValue.getQuestionName().equalsIgnoreCase(question))) {
                  return assessmentScoringValue;
              }
          }
          return null;
      }

      private Map<String, List<String>> parseSurveyResult(String jsonResult) {
          Map<String, List<String>> questionAnswerMap = new HashMap<>();
          JSONParser parser = new org.json.simple.parser.JSONParser();
          JSONObject obj;
          Set keySet;
          try {
              obj = (JSONObject) parser.parse(jsonResult);
              keySet = obj.keySet();
              for (Object question : keySet) {
                  Object answerObject = obj.get(question);
                  if (answerObject instanceof String) {
                      String answer = (String) obj.get(question);
                      List<String> answers = new ArrayList<>();
                      answers.add(answer);
                      questionAnswerMap.put((String) question, answers);
                  } else if (answerObject instanceof JSONArray) {
                      List<String> answers = (List<String>) obj.get(question);
                      questionAnswerMap.put((String) question, answers);
                  }
              }
          } catch (ParseException e) {
              e.printStackTrace();
          }
          return questionAnswerMap;
      }

      private List<ResidentAssessmentResultListDto> toAssessmentListDto(List<ResidentAssessmentResult> content) {
          final List<ResidentAssessmentResultListDto> dtos = new ArrayList<>();
          for (ResidentAssessmentResult source : content) {
              Boolean canEditAssessmentResults = canEditAssessmentResults(source.getAssessment().getDatabases());
              dtos.add(transformListItem(source, canEditAssessmentResults));
          }
          return dtos;
      }

      private boolean canEditAssessmentResults(List<Database> assessmentDatabases) {
          Boolean result = false;
          if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
              return true;
          }
          ExchangeUserDetails details = SecurityUtils.getAuthenticatedUser();
          Set<Long> employeeIds = details.getEmployeeAndLinkedEmployeeIds();
          if (CollectionUtils.isNotEmpty(assessmentDatabases)) {
              Set<Long> currentDatabaseIds = SecurityUtils.getAuthenticatedUser().getCurrentAndLinkedDatabaseIds();
              Boolean assessmentShared = false;
              for (Database database : assessmentDatabases) {
                  if (currentDatabaseIds.contains(database.getId())) {
                      assessmentShared = true;
                  }
              }
              if (!assessmentShared) {
                  return false;
              }
          }
          for (Long employeeId : employeeIds) {
              Set<GrantedAuthority> currentEmployeeAuthorities = details.getEmployeeAuthoritiesMap().get(employeeId);
              if (SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_CAN_ADD_EDIT_ASSESSMENTS_RESULTS)) {
                  result = true;
                  break;
              }
          }
          return result;
      }

      private ResidentAssessmentResultListDto transformListItem(ResidentAssessmentResult source, Boolean canEditAssessmentResults) {
          //TODO draft version
          ResidentAssessmentResultListDto dto = new ResidentAssessmentResultListDto();
          dto.setId(source.getId());
          dto.setAssessmentName(source.getAssessment().getShortName());
          dto.setStatus(source.getAssessment_status().getDisplayName());
          dto.setDateCompleted(source.getDateCompleted());
          dto.setDateAssigned(source.getDateAssigned());
          dto.setAuthor(source.getEmployee().getFullName());
          if (BooleanUtils.isTrue(canEditAssessmentResults)) {
              dto.setEditable(canEditAssessmentResults);
          }
          dto.setAssessmentId(source.getAssessment().getId());
          dto.setCanBeDownloaded(source.getAssessment().getCode().equals("COMPREHENSIVE"));
          return dto;
      }

      /*@Deprecated
      private ResidentAssessmentResult fromResidentAssessmentResultDto(ResidentAssessmentResultDto residentAssessmentResultDto) {
          ResidentAssessmentResult residentAssessmentResult = new ResidentAssessmentResult();
          residentAssessmentResult.setArchived(Boolean.FALSE);
          residentAssessmentResult.setAssessment(assessmentDao.getOne(residentAssessmentResultDto.getAssessmentId()));
          residentAssessmentResult.setComment(residentAssessmentResultDto.getComment());
          //assigned date set equal to completed when creating result from web
          residentAssessmentResult.setDateAssigned(residentAssessmentResultDto.getDateCompleted());
          residentAssessmentResult.setDateCompleted(residentAssessmentResultDto.getDateCompleted());
          residentAssessmentResult.setEmployee(SecurityUtils.getAuthenticatedUser().getEmployee());
          residentAssessmentResult.setResident(residentDao.getResident(residentAssessmentResultDto.getPatientId()));
          residentAssessmentResult.setResult(residentAssessmentResultDto.getResultJson());
          return residentAssessmentResult;
      }*/

      private Set<Long> getMatchedResidentIds(Long patientId) {
          final Set<Long> mergedFilterResidentsIds = new HashSet<Long>();
          mergedFilterResidentsIds.add(patientId);
          mergedFilterResidentsIds.addAll(mpiService.listMergedResidents(patientId));
          return mergedFilterResidentsIds;
      }


  }

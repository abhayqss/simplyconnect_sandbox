package com.scnsoft.eldermark.service;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.beans.IncidentReportFilter;
import com.scnsoft.eldermark.beans.projection.ConversationSidAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.IncidentReportSecurityAwareEntity;
import com.scnsoft.eldermark.dao.ClientCareTeamMemberDao;
import com.scnsoft.eldermark.dao.CommunityCareTeamMemberDao;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.IncidentReportDao;
import com.scnsoft.eldermark.dao.specification.AuditableEntitySpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.EmployeeSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.IncidentReportSpecificationGenerator;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.IncidentReportStatus;
import com.scnsoft.eldermark.entity.document.DocumentReport;
import com.scnsoft.eldermark.entity.event.incident.*;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.service.basic.BaseAuditableService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class IncidentReportServiceImpl extends BaseAuditableService<IncidentReport> implements IncidentReportService {

    @Autowired
    private IncidentReportDao incidentReportDao;

    @Autowired
    private IncidentReportPdfGenerationService incidentReportPdfGenerationService;

    @Autowired
    private ClientCareTeamMemberDao clientCareTeamMemberDao;

    @Autowired
    private CommunityCareTeamMemberDao communityCareTeamMemberDao;

    @Autowired
    private IncidentReportSpecificationGenerator incidentReportSpecificationGenerator;

    @Autowired
    private IncidentReportSubmitNotificationService incidentReportEmployeeNotificationService;

    @Autowired
    private EmployeeSpecificationGenerator employeeSpecificationGenerator;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private AuditableEntitySpecificationGenerator<IncidentReport> auditableEntitySpecificationGenerator;

    @Override
    @Transactional(readOnly = true)
    public Page<IncidentReport> find(IncidentReportFilter filter, PermissionFilter predicatePermissionFilter, Pageable pageable) {
        var byFilter = incidentReportSpecificationGenerator.byFilter(filter);
        var hasAccess = incidentReportSpecificationGenerator.hasAccess(predicatePermissionFilter);
        var isUnarchived = incidentReportSpecificationGenerator.isUnarchived();
        return incidentReportDao.findAll(byFilter.and(isUnarchived).and(hasAccess), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IncidentReport> findHistoryById(Long id, Pageable pageable) {
        return incidentReportDao.findAll(auditableEntitySpecificationGenerator.historyById(id), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentReport findByEventId(Long eventId) {
        return incidentReportDao.findByEvent_IdAndArchivedIsFalse(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentReport writePDFById(Long id, HttpServletResponse response, ZoneId zoneId) {
        try {
            IncidentReport incidentReport = incidentReportDao.findById(id).orElseThrow();
            return incidentReportPdfGenerationService.generatePdfReport(incidentReport, zoneId);
        } catch (DocumentException e) {
            throw new InternalServerException(InternalServerExceptionType.PDF_GENERATION_ERROR);
        } catch (IOException e) {
            throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR);
        }
    }

    @Override
    public Long saveDraft(IncidentReport incidentReport) {
        incidentReport.setStatus(IncidentReportStatus.DRAFT);
        return saveIncidentReport(incidentReport);
    }

    @Override
    public Long submit(IncidentReport incidentReport) {
        incidentReport.setSubmitted(true);
        incidentReport.setStatus(IncidentReportStatus.SUBMITTED);
        Long id = saveIncidentReport(incidentReport);
        incidentReportEmployeeNotificationService.sendNotifications(incidentReport);
        return id;
    }

    private Long saveIncidentReport(IncidentReport incidentReport) {
        if (incidentReport.getId() == null) {
            return createAuditableEntity(incidentReport);
        } else {
            return updateAuditableEntity(incidentReport);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getResidentCareTeamMemberRoleByEmployeeIdAndResidentId(Long employeeId, Long residentId) {
        try {
            return clientCareTeamMemberDao.findByEmployeeIdAndClientId(employeeId, residentId).getCareTeamRole().getName();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getCommunityCareTeamMemberRoleByEmployeeId(Long employeeId, Long organizationId) {
        try {
            return communityCareTeamMemberDao.findAllByCommunityIdAndEmployeeId(organizationId, employeeId).get(0).getCareTeamRole().getName();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public IncidentReport save(IncidentReport entity) {
        return incidentReportDao.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentReport findById(Long id) {
        return incidentReportDao.findById(id).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public Long findIncidentReportId(Long eventId) {
        return incidentReportDao.findByEventIdAndArchivedIsFalse(eventId).map(IdAware::getId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findReviewers(IncidentReport incidentReport) {
        var byOrganizationId = employeeSpecificationGenerator.byOrganizationId(incidentReport.getEvent().getClient().getOrganization().getId());
        var isActive = employeeSpecificationGenerator.active();
        var isIncidentReportReviewer = employeeSpecificationGenerator.isIncidentReportReviewer();
        return employeeDao.findAll(byOrganizationId.and(isActive.and(isIncidentReportReviewer)));
    }

    @Override
    public void deleteById(Long id) {
        IncidentReport entity = incidentReportDao.getOne(id);
        entity.setStatus(IncidentReportStatus.DELETED);
        deleteAuditableEntity(entity);
    }


    @Override
    public IncidentReport createTransientClone(IncidentReport entity) {
        var clone = new IncidentReport();
        clone.setBirthDate(entity.getBirthDate());
        clone.setAgencyResponseToIncident(entity.getAgencyResponseToIncident());
        clone.setFirstName(entity.getFirstName());
        clone.setLastName(entity.getLastName());
        clone.setCareManagerOrStaffEmail(entity.getCareManagerOrStaffEmail());
        clone.setCareManagerOrStaffPhone(entity.getCareManagerOrStaffPhone());
        clone.setClassMemberType(entity.getClassMemberType());
        clone.setClassMemberCurrentAddress(entity.getClassMemberCurrentAddress());
        clone.setCareManagerOrStaffWithPrimServRespAndTitle(entity.getCareManagerOrStaffWithPrimServRespAndTitle());
        clone.setEmployee(entity.getEmployee());
        clone.setUnitNumber(entity.getUnitNumber());
        clone.setClientPhone(entity.getClientPhone());
        clone.setSiteName(entity.getSiteName());
        clone.setEvent(entity.getEvent());
        clone.setGender(entity.getGender());
        clone.setRace(entity.getRace());
        clone.setRin(entity.getRin());
        clone.setTransitionToCommunityDate(entity.getTransitionToCommunityDate());
        clone.setAgencyName(entity.getAgencyName());
        clone.setAgencyAddress(entity.getAgencyAddress());
        clone.setQualityAdministrator(entity.getQualityAdministrator());
        clone.setMcoCareCoordinatorAndAgency(entity.getMcoCareCoordinatorAndAgency());
        clone.setMcoCareCoordinatorEmail(entity.getMcoCareCoordinatorEmail());
        clone.setMcoCareCoordinatorPhone(entity.getMcoCareCoordinatorPhone());
        clone.setIncidentDatetime(entity.getIncidentDatetime());
        clone.setIncidentDiscoveredDate(entity.getIncidentDiscoveredDate());
        clone.setWasProviderPresentOrScheduled(entity.getWasProviderPresentOrScheduled());
        clone.setWasIncidentCausedBySubstance(entity.getWasIncidentCausedBySubstance());
        clone.setWasIncidentParticipantTakenToHospital(entity.getWasIncidentParticipantTakenToHospital());
        clone.setNarrative(entity.getNarrative());
        clone.setAgencyResponseToIncident(entity.getAgencyResponseToIncident());
        clone.setReportAuthor(entity.getReportAuthor());
        clone.setReportAuthorTitle(entity.getReportAuthorTitle());
        clone.setReportAuthorPhone(entity.getReportAuthorPhone());
        clone.setReportedBy(entity.getReportedBy());
        clone.setReportedByTitle(entity.getReportedByTitle());
        clone.setReportedByPhone(entity.getReportedByPhone());
        clone.setWereApparentInjuries(entity.getWereApparentInjuries());
        clone.setInjuredClientCondition(entity.getInjuredClientCondition());
        clone.setVitalSigns(entity.getVitalSigns());
        clone.setImmediateIntervention(entity.getImmediateIntervention());
        clone.setFollowUpInformation(entity.getFollowUpInformation());
        clone.setSubmitted(entity.getSubmitted());
        clone.setStatus(entity.getStatus());
        clone.setReportCompletedDate(entity.getReportCompletedDate());
        clone.setReportDate(entity.getReportDate());
        clone.setWitnesses(entity.getWitnesses());
        clone.setAgencyAddress(entity.getAgencyAddress());
        clone.setNotifications(cloneNotifications(entity.getNotifications(), clone));
        clone.setIncidentInjuries(cloneInjuries(entity.getIncidentInjuries(), clone));
        clone.setIncidentTypes(cloneIncidentTypes(entity.getIncidentTypes(), clone));
        clone.setIncidentPlaceTypes(cloneIncidentPlaceType(entity.getIncidentPlaceTypes(), clone));
        clone.setIncidentWeatherConditionTypes(cloneIncidentWeatherTypes(entity.getIncidentWeatherConditionTypes(), clone));
        clone.setIndividuals(cloneIndividuals(entity.getIndividuals(), clone));
        clone.setWitnesses(cloneIncidentWitness(entity.getWitnesses(), clone));
        clone.setPictures(cloneIncidentPictures(entity.getPictures(), clone));
        clone.setTwilioConversationSid(entity.getTwilioConversationSid());
        return clone;
    }

    @Override
    public Optional<Instant> findOldestDateByOrganization(IncidentReportFilter filter, PermissionFilter predicatePermissionFilter) {
        return incidentReportDao.findMinDate(incidentReportSpecificationGenerator
                .byFilter(filter)
                .and(incidentReportSpecificationGenerator.hasAccess(predicatePermissionFilter)));
    }

    @Override
    public Optional<Instant> findNewestDateByOrganization(IncidentReportFilter filter, PermissionFilter predicatePermissionFilter) {
        return incidentReportDao.findMaxDate(incidentReportSpecificationGenerator
                .byFilter(filter)
                .and(incidentReportSpecificationGenerator.hasAccess(predicatePermissionFilter)));
    }

    private List<IncidentReportNotification> cloneNotifications(List<IncidentReportNotification> notifications, IncidentReport cloneableIncidentReport) {
        return notifications.stream()
                .map(notification -> {
                    var clone = new IncidentReportNotification();
                    clone.setDestination(notification.getDestination());
                    clone.setDatetime(notification.getDatetime());
                    clone.setByWhom(notification.getByWhom());
                    clone.setFullName(notification.getFullName());
                    clone.setPhone(notification.getPhone());
                    clone.setResponse(notification.getResponse());
                    clone.setResponseDatetime(notification.getResponseDatetime());
                    clone.setComment(notification.getComment());
                    clone.setNotified(notification.getNotified());
                    clone.setIncidentReport(cloneableIncidentReport);
                    return clone;
                }).collect(Collectors.toList());
    }

    private List<IncidentReportIncidentTypeFreeText> cloneIncidentTypes(List<IncidentReportIncidentTypeFreeText> incidentTypes, IncidentReport cloneableIncidentReport) {
        return incidentTypes.stream()
                .map(incidentType -> {
                    var clone = new IncidentReportIncidentTypeFreeText();
                    clone.setIncidentReport(cloneableIncidentReport);
                    clone.setIncidentType(incidentType.getIncidentType());
                    clone.setFreeText(incidentType.getFreeText());
                    return clone;
                }).collect(Collectors.toList());
    }

    private List<IncidentReportIncidentPlaceTypeFreeText> cloneIncidentPlaceType(List<IncidentReportIncidentPlaceTypeFreeText> incidentPlaceTypes, IncidentReport cloneableIncidentReport) {
        return incidentPlaceTypes.stream()
                .map(incidentPlaceType -> {
                    var clone = new IncidentReportIncidentPlaceTypeFreeText();
                    clone.setFreeText(incidentPlaceType.getFreeText());
                    clone.setIncidentPlaceType(incidentPlaceType.getIncidentPlaceType());
                    clone.setIncidentReport(cloneableIncidentReport);
                    return clone;
                })
                .collect(Collectors.toList());
    }

    private List<IncidentWeatherConditionTypeFreeText> cloneIncidentWeatherTypes(List<IncidentWeatherConditionTypeFreeText> incidentWeatherConditionTypes, IncidentReport cloneableIncidentReport) {
        return incidentWeatherConditionTypes.stream()
                .map(incidentWeatherConditionTypeFreeText -> {
                    var clone = new IncidentWeatherConditionTypeFreeText();
                    clone.setIncidentReport(cloneableIncidentReport);
                    clone.setFreeText(incidentWeatherConditionTypeFreeText.getFreeText());
                    clone.setIncidentWeatherConditionType(incidentWeatherConditionTypeFreeText.getIncidentWeatherConditionType());
                    return clone;
                }).collect(Collectors.toList());
    }

    private List<Individual> cloneIndividuals(List<Individual> individuals, IncidentReport cloneableIncidentReport) {
        return individuals.stream()
                .map(individual -> {
                    var clone = new Individual();
                    clone.setIncidentReport(cloneableIncidentReport);
                    clone.setName(individual.getName());
                    clone.setPhone(individual.getPhone());
                    clone.setRelationship(individual.getRelationship());
                    return clone;
                }).collect(Collectors.toList());
    }

    private List<IncidentWitness> cloneIncidentWitness(List<IncidentWitness> incidentWitnesses, IncidentReport cloneableIncidentReport) {
        return incidentWitnesses.stream()
                .map(incidentWitness -> {
                    var clone = new IncidentWitness();
                    clone.setReport(incidentWitness.getReport());
                    clone.setIncidentReport(cloneableIncidentReport);
                    clone.setName(incidentWitness.getName());
                    clone.setPhone(incidentWitness.getPhone());
                    clone.setRelationship(incidentWitness.getRelationship());
                    return clone;
                }).collect(Collectors.toList());
    }


    private List<IncidentPicture> cloneIncidentPictures(List<IncidentPicture> incidentPictures, IncidentReport cloneableIncidentReport) {
        return incidentPictures.stream()
                .map(incidentPicture -> {
                    var clone = new IncidentPicture();
                    clone.setIncidentReport(cloneableIncidentReport);
                    clone.setFileName(incidentPicture.getFileName());
                    clone.setMimeType(incidentPicture.getMimeType());
                    clone.setOriginalFileName(incidentPicture.getOriginalFileName());
                    return clone;
                }).collect(Collectors.toList());
    }

    private List<IncidentInjury> cloneInjuries(List<IncidentInjury> incidentInjuries, IncidentReport cloneableIncidentReport) {
        return incidentInjuries.stream()
                .map(incidentInjury -> {
                    var clone = new IncidentInjury();
                    clone.setIncidentReport(cloneableIncidentReport);
                    clone.setX(incidentInjury.getX());
                    clone.setY(incidentInjury.getY());
                    return clone;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentReportSecurityAwareEntity findSecurityAwareEntity(Long id) {
        return incidentReportDao.findById(id, IncidentReportSecurityAwareEntity.class).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidentReportSecurityAwareEntity> findSecurityAwareEntities(Collection<Long> ids) {
        return incidentReportDao.findByIdIn(ids, IncidentReportSecurityAwareEntity.class);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateHasNoConversation(Long id) {
        var conversationSidAware = findById(id, ConversationSidAware.class);
        if (StringUtils.isNotEmpty(conversationSidAware.getTwilioConversationSid())) {
            throw new BusinessException(BusinessException.CONSTRAINT_VIOLATION_CODE, "Incident Report already has conversation assigned");
        }
    }

    @Override
    public void assignConversation(Long id, String conversationSid) {
        validateHasNoConversation(id);
        incidentReportDao.assignConversation(id, conversationSid);
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long aLong, Class<P> projection) {
        return incidentReportDao.findById(aLong, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> longs, Class<P> projection) {
        return incidentReportDao.findByIdIn(longs, projection);
    }
}

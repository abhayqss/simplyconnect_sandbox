package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.NoteSecurityAwareEntity;
import com.scnsoft.eldermark.dao.NoteDao;
import com.scnsoft.eldermark.dao.specification.EmployeeSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.NoteSpecificationGenerator;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.IdNamesAware;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.entity.note.NoteDashboardItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class NoteServiceImpl extends BaseNoteService<Note> implements NoteService {

    private static final Set<CareTeamRoleCode> CONTACT_ROLES_TO_EXCLUDE = Set.of(
            CareTeamRoleCode.ROLE_CONTENT_CREATOR
    );

    @Autowired
    private NoteDao noteDao;

    @Autowired
    private NoteSpecificationGenerator noteSpecificationGenerator;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeSpecificationGenerator employeeSpecificationGenerator;

    @Autowired
    private ClientService clientService;

    @Override
    @Transactional
    public Note save(Note entity) {
        return noteDao.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Note findById(Long noteId) {
        return noteDao.findById(noteId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Note> findHistory(Long noteId, Pageable pageRequest) {
        var note = noteDao.findById(noteId).orElseThrow();
        if (note.getChainId() != null)
            return noteDao.findByIdOrChainId(note.getChainId(), note.getChainId(), pageRequest);
        else
            return noteDao.findByIdOrChainId(note.getId(), note.getId(), pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Note> findEventNotes(Long eventId, Pageable pageable) {
        return noteDao.findAllByEvent_IdAndArchivedIsFalse(eventId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getTakenNoteTypeIds(Long clientId, Long admittanceHistoryId) {
        if (CareCoordinationConstants.ADMIT_DATE_FROM_INTAKE_DATE_ID.equals(admittanceHistoryId)) {
            return noteDao.findTakenNoteTypeIdsByClientIdAndIntakeDateIsNotNull(clientId);
        } else {
            return noteDao.findTakenNoteTypeIdsByClientIdAndAdmittanceHistoryId(clientId, admittanceHistoryId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAdmitDateCanBeTaken(Long clientId, Long subTypeId, Long noteId, Long admittanceHistoryId) {
        if (noteId == null) {
            if (CareCoordinationConstants.ADMIT_DATE_FROM_INTAKE_DATE_ID.equals(admittanceHistoryId)) {
                return !noteDao.existsByClient_IdAndSubType_IdAndIntakeDateIsNotNullAndArchivedIsFalse(clientId, subTypeId);
            } else {
                return !noteDao.existsByClient_IdAndSubType_IdAndAdmittanceHistory_IdAndArchivedIsFalse(clientId, subTypeId, admittanceHistoryId);
            }
        } else {
            if (CareCoordinationConstants.ADMIT_DATE_FROM_INTAKE_DATE_ID.equals(admittanceHistoryId)) {
                return !noteDao.existsByClient_IdAndSubType_IdAndIdNotAndIntakeDateIsNotNullAndArchivedIsFalse(clientId, subTypeId, noteId);
            } else {
                return !noteDao.existsByClient_IdAndSubType_IdAndIdNotAndAdmittanceHistory_IdAndArchivedIsFalse(clientId, subTypeId, noteId, admittanceHistoryId);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteDashboardItem> find(Long clientId, PermissionFilter permissionFilter, Integer limit, Sort sort) {
        var byClientAndHasAccess = noteSpecificationGenerator.byClientIdAndMergedAndHasAccessAndDistinct(permissionFilter, clientId);
        var unarchived = noteSpecificationGenerator.isUnarchived();
        return noteDao.findAll(byClientAndHasAccess.and(unarchived), NoteDashboardItem.class, sort, limit);
    }

    @Override
    public <P> List<P> find(Specification<Note> specification, Class<P> projectionClass) {
        return noteDao.findAll(specification, projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdNamesAware> getAvailableContactNames(Long clientId) {

        var client = clientService.findById(clientId, OrganizationIdAware.class);

        return employeeService.findAll(
                employeeSpecificationGenerator.active()
                        .and(employeeSpecificationGenerator.inEligibleForDiscoveryCommunity())
                        .and(Specification.not(employeeSpecificationGenerator.isOnHoldCtmForClient(clientId)))
                        .and(employeeSpecificationGenerator.systemRoleNotIn(CONTACT_ROLES_TO_EXCLUDE))
                        .and(employeeSpecificationGenerator.byOrganizationId(client.getOrganizationId())),
                IdNamesAware.class
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdNamesAware> getAvailableContactNamesForGroupNote(Long organizationId) {
        return employeeService.findAll(
                employeeSpecificationGenerator.active()
                        .and(employeeSpecificationGenerator.inEligibleForDiscoveryCommunity())
                        .and(employeeSpecificationGenerator.systemRoleNotIn(CONTACT_ROLES_TO_EXCLUDE))
                        .and(employeeSpecificationGenerator.byOrganizationId(organizationId)),
                IdNamesAware.class
        );
    }

    @Override
    @Transactional(readOnly = true)
    public NoteSecurityAwareEntity findSecurityAwareEntity(Long id) {
        return findById(id, NoteSecurityAwareEntity.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteSecurityAwareEntity> findSecurityAwareEntities(Collection<Long> ids) {
        return findAllById(ids, NoteSecurityAwareEntity.class);
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long id, Class<P> projection) {
        return noteDao.findById(id, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return noteDao.findByIdIn(ids, projection);
    }
}

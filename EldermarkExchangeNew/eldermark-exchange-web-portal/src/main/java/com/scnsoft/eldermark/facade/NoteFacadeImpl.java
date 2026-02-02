package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.EventNoteFilter;
import com.scnsoft.eldermark.beans.NoteStatisticsFilterDto;
import com.scnsoft.eldermark.beans.security.projection.dto.NoteSecurityFieldsAware;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.events.EventOrNoteListItemDto;
import com.scnsoft.eldermark.dto.notes.*;
import com.scnsoft.eldermark.entity.note.*;
import com.scnsoft.eldermark.entity.projection.EncounterNoteCount;
import com.scnsoft.eldermark.service.*;
import com.scnsoft.eldermark.service.security.NoteSecurityService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteFacadeImpl implements NoteFacade {

    private static final Sort.Order LAST_MODIFIED_DESC_SORT_ORDER = new Sort.Order(Sort.Direction.DESC,
            Note_.LAST_MODIFIED_DATE);

    @Autowired
    private NoteService noteService;

    @Autowired
    private Converter<Note, NoteDto> noteDtoConverter;

    @Autowired
    private ListAndItemConverter<Note, NoteHistoryListItemDto> noteHistoryListItemDtoConverter;

    @Autowired
    private ListAndItemConverter<Note, RelatedNoteListItemDto> relatedNotedtoConverter;

    @Autowired
    private EncounterNoteService encounterNoteService;

    @Autowired
    private Converter<NoteDto, Note> noteEntityConverter;

    @Autowired
    private Converter<NoteDto, EncounterNote> encounterNoteDtoToEntityConverter;

    @Autowired
    private ListAndItemConverter<EncounterNoteCount, EntityStatisticsDto> encounterNoteCountEntityToDtoListItemConverter;

    @Autowired
    private ClientService clientService;

    @Autowired
    private EventNoteService eventNoteService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private NoteSecurityService noteSecurityService;

    @Autowired
    private Converter<NoteDto, ServiceStatusCheckNote> serviceStatusCheckNoteDtoToEntityConverter;

    @Autowired
    private ListAndItemConverter<NoteDashboardItem, NoteDashboardListItemDto> noteDashboardListItemDtoConverter;

    @Autowired
    private ClientProgramNoteService clientProgramNoteService;

    @Autowired
    private Converter<NoteDto, ClientProgramNote> clientProgramDtoToEntityConverter;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@noteSecurityService.canView(#noteId)")
    public NoteDto findById(@P("noteId") Long noteId) {
        return noteDtoConverter.convert(noteService.findById(noteId));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@noteSecurityService.canViewList()")
    public Long findPageNumber(Long noteId, EventNoteFilter eventNoteFilter, int pageSize) {
        var sort = PaginationUtils.findDefaultEntitySort(EventOrNoteListItemDto.class).orElseThrow();
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        return eventNoteService.findNotePageNumber(noteId, eventNoteFilter, permissionFilter, pageSize, sort);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@noteSecurityService.canViewList()")
    public List<NoteDashboardListItemDto> findNotesForDashboard(Long clientId, Integer limit) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var sort = PaginationUtils.findDefaultEntitySort(NoteDashboardListItemDto.class).orElse(null);
        var notes =  noteService.find(clientId, permissionFilter, limit, sort);
        return noteDashboardListItemDtoConverter.convertList(notes);
    }

    @Override
    @Transactional
    @PreAuthorize("@noteSecurityService.canAdd(#noteDto)")
    public Long add(@P("noteDto") NoteDto noteDto) {
        if (noteDto.getClientId() != null) {
            clientService.validateActive(noteDto.getClientId());
        }
        if (noteDto.getEncounter() != null && noteDto.getEncounter().getTypeId() != null) {
            return encounterNoteService.createAuditableEntity(encounterNoteDtoToEntityConverter.convert(noteDto));
        } else  if (noteDto.getServiceStatusCheck() != null) {
            return noteService.createAuditableEntity(serviceStatusCheckNoteDtoToEntityConverter.convert(noteDto));
        } else if (noteDto.getClientProgram() != null) {
            return clientProgramNoteService.createAuditableEntity(clientProgramDtoToEntityConverter.convert(noteDto));
        }
        else {
            return noteService.createAuditableEntity(noteEntityConverter.convert(noteDto));
        }
    }

    @Override
    @Transactional
    @PreAuthorize("@noteSecurityService.canEdit(#noteDto.id)")
    public Long edit(@P("noteDto") NoteDto noteDto) {
        if (noteDto.getClientId() != null) {
            clientService.validateActive(noteDto.getClientId());
        }
        if (noteDto.getEncounter() != null && noteDto.getEncounter().getTypeId() != null) {
            return encounterNoteService.updateAuditableEntity(encounterNoteDtoToEntityConverter.convert(noteDto));
        } else  if (noteDto.getServiceStatusCheck() != null) {
            return noteService.updateAuditableEntity(serviceStatusCheckNoteDtoToEntityConverter.convert(noteDto));
        } else if (noteDto.getClientProgram() != null) {
            return clientProgramNoteService.updateAuditableEntity(clientProgramDtoToEntityConverter.convert(noteDto));
        } else {
            return noteService.updateAuditableEntity(noteEntityConverter.convert(noteDto));
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@noteSecurityService.canView(#noteId)")
    public Page<NoteHistoryListItemDto> history(@P("noteId") Long noteId, Pageable pageRequest) {
        var pageSorted = PaginationUtils.setSort(pageRequest, LAST_MODIFIED_DESC_SORT_ORDER);
        return noteService.findHistory(noteId, pageSorted).map(noteHistoryListItemDtoConverter::convert);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@eventSecurityService.canView(#eventId)")
    public Page<RelatedNoteListItemDto> findRelatedNotes(@P("eventId") Long eventId, Pageable pageRequest) {
        var pageSorted = PaginationUtils.setSort(pageRequest, LAST_MODIFIED_DESC_SORT_ORDER);
        var relatedNotes = noteService.findEventNotes(eventId, pageSorted);
        return relatedNotes.map(relatedNotedtoConverter::convert);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@noteSecurityService.canViewList()")
    public List<EntityStatisticsDto> getEncounterNoteCount(NoteStatisticsFilterDto filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return encounterNoteCountEntityToDtoListItemConverter.convertList(encounterNoteService.count(filter, permissionFilter));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canView(#clientId)")
    public List<AdmitDateDto> findAdmitDates(@P("clientId") Long clientId) {
        var admitDates = clientService.findAdmitIntakeDates(clientId);
        return admitDates.stream()
                .map(admit -> new AdmitDateDto(admit.getId(), DateTimeUtils.toEpochMilli(admit.getAdmitIntakeDate()), noteService.getTakenNoteTypeIds(clientId, admit.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canView(#clientId)")
    public List<IdentifiedNamedEntityDto> findAvailableContacts(Long clientId) {
        return noteService.getAvailableContactNames(clientId).stream()
                .map(it -> new IdentifiedNamedEntityDto(it.getId(), it.getFullName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdentifiedNamedEntityDto> findAvailableContactsForGroupNote(Long organizationId) {
        return noteService.getAvailableContactNamesForGroupNote(organizationId).stream()
                .map(it -> new IdentifiedNamedEntityDto(it.getId(), it.getFullName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAddClientNote(Long clientId) {
        return noteSecurityService.canAdd(new NoteSecurityFieldsAware() {
            @Override
            public Long getEventId() {
                return null;
            }

            @Override
            public List<Long> getClientIds() {
                return null;
            }

            @Override
            public Long getClientId() {
                return clientId;
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAddEventNote(Long eventId) {
        return noteSecurityService.canAdd(new NoteSecurityFieldsAware() {
            @Override
            public Long getEventId() {
                return eventId;
            }

            @Override
            public List<Long> getClientIds() {
                return null;
            }

            @Override
            public Long getClientId() {
                return null;
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canViewList() {
        return noteSecurityService.canViewList();
    }
}

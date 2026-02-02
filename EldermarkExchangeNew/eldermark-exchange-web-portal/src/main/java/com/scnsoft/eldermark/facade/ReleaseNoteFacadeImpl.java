package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.ReleaseNoteDto;
import com.scnsoft.eldermark.dto.ReleaseNoteListItemDto;
import com.scnsoft.eldermark.dto.notifications.inapp.InAppNotificationDto;
import com.scnsoft.eldermark.entity.ReleaseNote;
import com.scnsoft.eldermark.service.ReleaseNoteService;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.service.security.ReleaseNoteSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReleaseNoteFacadeImpl implements ReleaseNoteFacade {

    @Autowired
    private ReleaseNoteService releaseNoteService;

    @Autowired
    private Converter<ReleaseNote, ReleaseNoteDto> releaseNoteDtoConverter;

    @Autowired
    private ReleaseNoteSecurityService releaseNoteSecurityService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private Converter<ReleaseNoteDto, ReleaseNote> releaseNoteConverter;

    @Autowired
    private Converter<ReleaseNote, InAppNotificationDto> releaseNoteInAppNotificationDtoConverter;

    @Autowired
    private Converter<ReleaseNote, ReleaseNoteListItemDto> releaseNoteListItemDtoConverter;

    @Override
    @Transactional(readOnly = true)
    public List<ReleaseNoteListItemDto> find() {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return releaseNoteService.find(permissionFilter)
                .stream()
                .map(releaseNoteListItemDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@releaseNoteSecurityService.canView()")
    public ReleaseNoteDto findById(Long id) {
        return releaseNoteDtoConverter.convert(releaseNoteService.findById(id));
    }

    @Override
    @PreAuthorize("@releaseNoteSecurityService.canUpload()")
    public Long save(ReleaseNoteDto dto) {
        return releaseNoteService.save(releaseNoteConverter.convert(dto), dto.getFile());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@releaseNoteSecurityService.canView()")
    public void downloadById(Long id, HttpServletResponse response) {
        var releaseNote = releaseNoteService.findById(id);
        var file = releaseNoteService.download(releaseNote);
        WriterUtils.copyFileContentToResponse(file, response);
    }

    @Override
    @PreAuthorize("@releaseNoteSecurityService.canDelete()")
    public boolean deleteById(Long id) {
        return releaseNoteService.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUpload() {
        return releaseNoteSecurityService.canUpload();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canDelete() {
        return releaseNoteSecurityService.canDelete();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canView() {
        return releaseNoteSecurityService.canView();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InAppNotificationDto> findReleaseNotificationsByCreatedDateAfterWithInAppEnabled(Instant createdDate) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return releaseNoteService.findByCreatedDateAfterWithInAppEnabled(createdDate, permissionFilter).stream()
                .map(releaseNoteInAppNotificationDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InAppNotificationDto> findLatestReleaseNotificationByCreatedDateAfterWithInAppEnabled(Instant createdDate) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return releaseNoteService.findLatestByCreatedDateAfterWithInAppEnabled(createdDate, permissionFilter)
                .map(releaseNoteInAppNotificationDtoConverter::convert);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InAppNotificationDto> findLatestReleaseNotificationWithInAppEnabled() {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return releaseNoteService.findLatestWithInAppEnabled(permissionFilter)
                .map(releaseNoteInAppNotificationDtoConverter::convert);
    }
}

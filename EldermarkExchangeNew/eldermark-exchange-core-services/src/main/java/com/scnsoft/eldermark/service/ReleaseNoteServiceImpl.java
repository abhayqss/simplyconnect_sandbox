package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ReleaseNoteDao;
import com.scnsoft.eldermark.entity.ReleaseNote;
import com.scnsoft.eldermark.entity.ReleaseNote_;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import com.scnsoft.eldermark.service.storage.ReleaseNoteFileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReleaseNoteServiceImpl implements ReleaseNoteService {

    @Autowired
    private ReleaseNoteDao releaseNoteDao;

    @Autowired
    private ReleaseNoteFileStorage releaseNoteFileStorage;

    @Override
    @Transactional(readOnly = true)
    public List<ReleaseNote> find(PermissionFilter permissionFilter) {
        if (!canViewList(permissionFilter)) {
            return Collections.emptyList();
        }
        return releaseNoteDao.findAll(Sort.by(Sort.Direction.DESC, ReleaseNote_.CREATED_DATE));
    }

    @Override
    @Transactional(readOnly = true)
    public ReleaseNote findById(Long id) {
        return releaseNoteDao.findById(id).orElseThrow();
    }

    @Override
    public WriterUtils.FileProvider download(ReleaseNote releaseNote) {
        return WriterUtils.FileProvider.of(
                releaseNote.getTitle(),
                releaseNote.getMimeType(),
                () -> releaseNoteFileStorage.loadAsInputStream(releaseNote.getFileName())
        );
    }

    @Override
    public Long save(ReleaseNote releaseNote, MultipartFile file) {
        if (releaseNote.getId() != null) {
            var saved = releaseNoteDao.findById(releaseNote.getId())
                    .orElseThrow(() -> new BusinessException(BusinessExceptionType.NOT_FOUND));

            releaseNoteFileStorage.delete(saved.getFileName());
            releaseNote.setCreatedDate(saved.getCreatedDate());
        }

        var fileName = releaseNoteFileStorage.save(file);
        releaseNote.setFileName(fileName);
        releaseNote.setMimeType(file.getContentType());
        releaseNote.setTitle(file.getOriginalFilename());

        return releaseNoteDao.save(releaseNote).getId();
    }

    @Override
    public boolean deleteById(Long id) {
        var releaseNote = releaseNoteDao.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessExceptionType.NOT_FOUND));

        releaseNoteDao.delete(releaseNote);
        return releaseNoteFileStorage.delete(releaseNote.getFileName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReleaseNote> findByCreatedDateAfterWithInAppEnabled(Instant createdDate, PermissionFilter permissionFilter) {
        if (!canViewList(permissionFilter)) {
            return Collections.emptyList();
        }
        return releaseNoteDao.findAllByCreatedDateAfterAndInAppNotificationEnabledTrue(createdDate, Sort.by(Sort.Direction.ASC, ReleaseNote_.CREATED_DATE));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReleaseNote> findLatestByCreatedDateAfterWithInAppEnabled(Instant createdDate, PermissionFilter permissionFilter) {
        if (!canViewList(permissionFilter)) {
            return Optional.empty();
        }
        return releaseNoteDao.findFirstByCreatedDateAfterAndInAppNotificationEnabledTrueOrderByCreatedDateDesc(createdDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReleaseNote> findLatestWithInAppEnabled(PermissionFilter permissionFilter) {
        if (!canViewList(permissionFilter)) {
            return Optional.empty();
        }
        return releaseNoteDao.findFirstByInAppNotificationEnabledTrueOrderByCreatedDateDesc();
    }

    @Override
    public boolean canViewList(PermissionFilter permissionFilter) {
        return permissionFilter.hasAnyPermission(Arrays.asList(
                Permission.ROLE_SUPER_ADMINISTRATOR,
                Permission.VIEW_RELEASE_NOTES));
    }

    @Override
    public <P> P findById(Long id, Class<P> projection) {
        return releaseNoteDao.findById(id, projection).orElseThrow();
    }

    @Override
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return releaseNoteDao.findByIdIn(ids, projection);
    }
}

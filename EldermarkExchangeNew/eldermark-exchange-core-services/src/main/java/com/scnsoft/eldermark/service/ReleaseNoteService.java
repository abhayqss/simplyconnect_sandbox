package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.ReleaseNote;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReleaseNoteService extends ProjectingService<Long>{

    List<ReleaseNote> find(PermissionFilter permissionFilter);

    ReleaseNote findById(Long id);

    WriterUtils.FileProvider download(ReleaseNote releaseNote);

    Long save(ReleaseNote releaseNote, MultipartFile file);

    boolean deleteById(Long id);

    List<ReleaseNote> findByCreatedDateAfterWithInAppEnabled(Instant createdDate, PermissionFilter permissionFilter);

    Optional<ReleaseNote> findLatestByCreatedDateAfterWithInAppEnabled(Instant createdDate, PermissionFilter permissionFilter);

    Optional<ReleaseNote> findLatestWithInAppEnabled(PermissionFilter permissionFilter);

    boolean canViewList(PermissionFilter permissionFilter);
}

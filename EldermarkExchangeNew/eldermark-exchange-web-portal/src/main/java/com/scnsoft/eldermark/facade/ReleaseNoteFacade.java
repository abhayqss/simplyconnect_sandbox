package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.ReleaseNoteDto;
import com.scnsoft.eldermark.dto.ReleaseNoteListItemDto;
import com.scnsoft.eldermark.dto.notifications.inapp.InAppNotificationDto;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReleaseNoteFacade {

    List<ReleaseNoteListItemDto> find();

    ReleaseNoteDto findById(Long id);

    Long save(ReleaseNoteDto dto);

    void downloadById(Long id, HttpServletResponse response);

    boolean deleteById(Long id);

    boolean canUpload();

    boolean canDelete();

    boolean canView();

    //TODO will be used in case multiple notifications will be shown
    List<InAppNotificationDto> findReleaseNotificationsByCreatedDateAfterWithInAppEnabled(Instant createdDate);

    Optional<InAppNotificationDto> findLatestReleaseNotificationByCreatedDateAfterWithInAppEnabled(Instant createdDate);

    Optional<InAppNotificationDto> findLatestReleaseNotificationWithInAppEnabled();
}

package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.ReleaseNote;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReleaseNoteDao extends AppJpaRepository<ReleaseNote, Long> {
    List<ReleaseNote> findAllByCreatedDateAfterAndInAppNotificationEnabledTrue(Instant createdDate, Sort sort);

    Optional<ReleaseNote> findFirstByInAppNotificationEnabledTrueOrderByCreatedDateDesc();

    Optional<ReleaseNote> findFirstByCreatedDateAfterAndInAppNotificationEnabledTrueOrderByCreatedDateDesc(Instant createdDate);
}

package com.scnsoft.eldermark.dao.video;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.video.VideoCallParticipantHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoCallParticipantHistoryDao extends AppJpaRepository<VideoCallParticipantHistory, Long> {
}

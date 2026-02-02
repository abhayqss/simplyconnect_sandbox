package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.security.projection.entity.AvatarSecurityAwareEntity;
import com.scnsoft.eldermark.dto.AvatarUpdateData;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.http.MediaType;

public interface AvatarService {

    Pair<byte[], MediaType> downloadById(Long id);

    void update(AvatarUpdateData data);

    void deleteById(Long id);

    AvatarSecurityAwareEntity findSecurityAware(Long id);
}

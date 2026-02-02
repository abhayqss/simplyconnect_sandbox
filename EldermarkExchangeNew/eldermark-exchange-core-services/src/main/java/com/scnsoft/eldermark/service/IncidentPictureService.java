package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.security.projection.entity.IncidentPictureSecurityAwareEntity;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

public interface IncidentPictureService {

    String save(MultipartFile picture);

    Pair<byte[], MediaType> downloadById(Long id);

    IncidentPictureSecurityAwareEntity findSecurityAware(Long id);
}

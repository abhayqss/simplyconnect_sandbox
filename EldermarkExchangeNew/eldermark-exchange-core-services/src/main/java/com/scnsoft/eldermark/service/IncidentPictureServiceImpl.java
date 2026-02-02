package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.security.projection.entity.IncidentPictureSecurityAwareEntity;
import com.scnsoft.eldermark.dao.IncidentPictureDao;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.service.storage.IncidentPictureFileStorage;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class IncidentPictureServiceImpl implements IncidentPictureService {

    @Autowired
    private IncidentPictureFileStorage incidentPictureFileStorage;

    @Autowired
    private IncidentPictureDao incidentPictureDao;

    @Override
    public String save(MultipartFile picture) {
        return incidentPictureFileStorage.save(picture);
    }

    @Override
    @Transactional(readOnly = true)
    public Pair<byte[], MediaType> downloadById(Long id) {
        var picture = incidentPictureDao.findById(id).orElseThrow();

        if (incidentPictureFileStorage.exists(picture.getFileName())) {
            return incidentPictureFileStorage.loadAsBytesWithMediaType(picture.getFileName());
        } else {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentPictureSecurityAwareEntity findSecurityAware(Long id) {
        return incidentPictureDao.findById(id, IncidentPictureSecurityAwareEntity.class).orElseThrow();
    }
}

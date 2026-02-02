package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.entity.event.incident.IncidentPicture;
import com.scnsoft.eldermark.service.IncidentPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class IncidentPictureEntityConverter extends IncidentReportEntityListConverter<MultipartFile, IncidentPicture> {

    @Autowired
    private IncidentPictureService incidentPictureService;

    @Override
    public IncidentPicture convert(MultipartFile source) {
        var fileName = incidentPictureService.save(source);
        if (fileName == null) {
            return null;
        }
        var target = new IncidentPicture();
        target.setOriginalFileName(source.getOriginalFilename());
        target.setFileName(fileName);
        target.setMimeType(source.getContentType());
        return target;
    }

}

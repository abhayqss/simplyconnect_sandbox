package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.UserManualDocumentDto;
import com.scnsoft.eldermark.entity.UserManual;
import com.scnsoft.eldermark.service.UserManualService;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import com.scnsoft.eldermark.service.security.UserManualSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserManualFacadeImpl implements UserManualFacade {

    @Autowired
    private UserManualService userManualService;

    @Autowired
    private Converter<UserManual, UserManualDocumentDto> userManualDocumentDtoConverter;

    @Autowired
    private UserManualSecurityService userManualSecurityService;

    @Override
    @PreAuthorize("@userManualSecurityService.canView()")
    public List<UserManualDocumentDto> find() {
        return userManualService.find()
                .stream()
                .map(userManualDocumentDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("@userManualSecurityService.canView()")
    public void downloadById(Long id, HttpServletResponse response) {
        var userManual = userManualService.findById(id);
        var file = userManualService.download(userManual);
        WriterUtils.copyFileContentToResponse(file, response);
    }

    @Override
    @PreAuthorize("@userManualSecurityService.canUpload()")
    public Long upload(UserManualDocumentDto dto) {

        var userManual = new UserManual();
        userManual.setId(dto.getId());
        userManual.setCreated(Instant.now());
        userManual.setTitle(dto.getTitle());

        return userManualService.save(userManual, dto.getFile());
    }

    @Override
    @PreAuthorize("@userManualSecurityService.canUpload()")
    public Long editById(UserManualDocumentDto dto, Long id) {
        dto.setId(id);
        return upload(dto);
    }

    @Override
    @PreAuthorize("@userManualSecurityService.canDelete()")
    public boolean deleteById(Long id) {
        return userManualService.deleteById(id);
    }

    @Override
    public boolean canUpload() {
        return userManualSecurityService.canUpload();
    }

    @Override
    public boolean canDelete() {
        return userManualSecurityService.canDelete();
    }

    @Override
    public boolean canView() {
        return userManualSecurityService.canView();
    }
}

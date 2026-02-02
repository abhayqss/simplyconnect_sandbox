package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.UserManualDocumentDto;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserManualFacade {


    List<UserManualDocumentDto> find();

    void downloadById(Long id, HttpServletResponse response);

    Long upload(UserManualDocumentDto dto);

    boolean deleteById(Long id);

    Long editById(UserManualDocumentDto dto, Long id);

    boolean canUpload();

    boolean canDelete();

    boolean canView();
}

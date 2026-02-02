package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.UserManual;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserManualService extends ProjectingService<Long> {

    List<UserManual> find();

    UserManual findById(Long id);

    WriterUtils.FileProvider download(UserManual userManual);

    Long save(UserManual userManual, MultipartFile file);

    boolean deleteById(Long id);
}

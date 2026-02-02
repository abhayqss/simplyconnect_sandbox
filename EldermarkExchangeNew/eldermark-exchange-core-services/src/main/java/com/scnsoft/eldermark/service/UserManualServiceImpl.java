package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.UserManualDao;
import com.scnsoft.eldermark.entity.UserManual;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import com.scnsoft.eldermark.service.storage.UserManualFileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

@Service
public class UserManualServiceImpl implements UserManualService {

    @Autowired
    private UserManualDao userManualDao;

    @Autowired
    private UserManualFileStorage userManualFileStorage;

    @Override
    @Transactional(readOnly = true)
    public List<UserManual> find() {
        return userManualDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public UserManual findById(Long id) {
        return userManualDao.findById(id).orElseThrow();
    }

    @Override
    public WriterUtils.FileProvider download(UserManual userManual) {
        return WriterUtils.FileProvider.of(
            userManual.getTitle(),
            userManual.getMimeType(),
            () -> userManualFileStorage.loadAsInputStream(userManual.getFileName())
        );
    }

    @Override
    @Transactional
    public Long save(UserManual userManual, MultipartFile file) {
        if (userManual.getId() != null) {
            var saved = userManualDao.findById(userManual.getId())
                .orElseThrow(() -> new BusinessException(BusinessExceptionType.NOT_FOUND));

            userManualFileStorage.delete(saved.getFileName());
            userManual.setCreated(saved.getCreated());
        }

        var fileName = userManualFileStorage.save(file);
        userManual.setFileName(fileName);
        userManual.setMimeType(file.getContentType());

        return userManualDao.save(userManual).getId();
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        var userManual = userManualDao.findById(id)
            .orElseThrow(() -> new BusinessException(BusinessExceptionType.NOT_FOUND));

        userManualDao.delete(userManual);

        return userManualFileStorage.delete(userManual.getFileName());
    }

    @Override
    public <P> P findById(Long id, Class<P> projection) {
        return userManualDao.findById(id, projection).orElseThrow();
    }

    @Override
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return userManualDao.findByIdIn(ids, projection);
    }
}

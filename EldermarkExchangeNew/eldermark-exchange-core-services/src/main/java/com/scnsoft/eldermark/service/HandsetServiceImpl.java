package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.HandsetDao;
import com.scnsoft.eldermark.entity.community.Handset;
import com.scnsoft.eldermark.service.basic.BaseAuditableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HandsetServiceImpl extends BaseAuditableService<Handset> implements HandsetService {

    @Autowired
    private HandsetDao handsetDao;

    @Override
    @Modifying
    public Handset save(Handset entity) {
        return handsetDao.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Handset findById(Long id) {
        return handsetDao.getOne(id);
    }
}

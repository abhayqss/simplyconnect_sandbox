package com.scnsoft.eldermark.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scnsoft.eldermark.dao.AdtMessageDao;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;

@Service
public class AdtMessageServiceImpl implements AdtMessageService {
    
    @Autowired
    private AdtMessageDao adtMessageDao;

    @Override
    @Transactional(readOnly = true)
    public AdtMessage findById(Long id) {
        return adtMessageDao.findById(id).orElseThrow();
    }

}

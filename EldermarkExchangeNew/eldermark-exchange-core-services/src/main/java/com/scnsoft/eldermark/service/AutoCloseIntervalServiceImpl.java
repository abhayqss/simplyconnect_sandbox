package com.scnsoft.eldermark.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scnsoft.eldermark.dao.AutoCloseIntervalDao;
import com.scnsoft.eldermark.entity.community.AutoCloseInterval;

@Service
@Transactional(readOnly=true)
public class AutoCloseIntervalServiceImpl implements AutoCloseIntervalService {
    
    @Autowired
    private AutoCloseIntervalDao autoCloseIntervalDao;
    
    @Override
    @Transactional(readOnly = true)
    public AutoCloseInterval findById(Long autoCloseIntervalId) {
        return autoCloseIntervalDao.findById(autoCloseIntervalId).orElse(null);
    }

}

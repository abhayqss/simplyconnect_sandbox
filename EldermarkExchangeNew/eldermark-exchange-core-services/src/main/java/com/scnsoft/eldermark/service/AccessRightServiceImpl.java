package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.AccessRightDao;
import com.scnsoft.eldermark.entity.AccessRight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class AccessRightServiceImpl implements AccessRightService {

    @Autowired
    private AccessRightDao accessRightDao;

    public Set<AccessRight> getDefaultAccessRights() {
        return new HashSet<>(accessRightDao.findAll());
    }
}

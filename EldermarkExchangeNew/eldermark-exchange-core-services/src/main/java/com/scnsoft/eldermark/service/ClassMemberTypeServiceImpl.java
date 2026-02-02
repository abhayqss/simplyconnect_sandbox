package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.ClassMemberTypeDao;
import com.scnsoft.eldermark.entity.event.incident.ClassMemberType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClassMemberTypeServiceImpl implements ClassMemberTypeService {

    @Autowired
    private ClassMemberTypeDao classMemberTypeDao;

    @Override
    @Transactional(readOnly = true)
    public ClassMemberType getById(Long id) {
        return classMemberTypeDao.getOne(id);
    }
}

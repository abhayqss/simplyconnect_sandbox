package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.SupportTicketTypeDao;
import com.scnsoft.eldermark.entity.SupportTicketType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SupportTicketTypeServiceImpl implements SupportTicketTypeService {

    @Autowired
    private SupportTicketTypeDao supportTicketTypeDao;

    @Override
    @Transactional(readOnly = true)
    public SupportTicketType findById(Long id) {
        return supportTicketTypeDao.getOne(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketType> findAll() {
        return supportTicketTypeDao.findAll();
    }
}

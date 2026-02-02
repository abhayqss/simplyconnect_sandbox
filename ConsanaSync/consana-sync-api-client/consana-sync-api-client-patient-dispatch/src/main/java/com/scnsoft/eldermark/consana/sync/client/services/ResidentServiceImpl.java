package com.scnsoft.eldermark.consana.sync.client.services;

import com.scnsoft.eldermark.consana.sync.client.dao.ResidentDao;
import com.scnsoft.eldermark.consana.sync.client.model.entities.Resident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ResidentServiceImpl implements ResidentService {

    private final ResidentDao residentDao;

    @Autowired
    public ResidentServiceImpl(ResidentDao residentDao) {
        this.residentDao = residentDao;
    }

    @Override
    public Optional<Resident> findById(Long residentId) {
        return residentDao.findById(residentId);
    }
}

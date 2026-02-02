package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.RaceDao;
import com.scnsoft.eldermark.entity.event.incident.Race;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RaceServiceImpl implements RaceService {

    @Autowired
    private RaceDao raceDao;

    @Override
    @Transactional(readOnly = true)
    public Race getById(Long id) {
        return raceDao.getOne(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Race findByCodeAndCodeSystem(String code, String codeSystem) {
        return raceDao.findByCodeAndCodeSystem(code, codeSystem).orElseThrow();
    }
}

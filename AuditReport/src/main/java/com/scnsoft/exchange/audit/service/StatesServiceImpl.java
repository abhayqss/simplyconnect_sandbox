package com.scnsoft.exchange.audit.service;

import com.scnsoft.exchange.audit.dao.StateDao;
import com.scnsoft.exchange.audit.model.StateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatesServiceImpl implements StatesService {

    @Autowired
    private StateDao stateDao;

    @Override
    public List<StateDto> getStates() {
        return stateDao.findAll();
    }
}

package com.scnsoft.eldermark.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scnsoft.eldermark.dao.StateDao;
import com.scnsoft.eldermark.entity.State;

@Service
@Transactional(readOnly = true)
public class StateServiceImpl implements StateService {

    @Autowired
    private StateDao stateDao;

    @Override
    public Optional<State> findById(Long id) {
        return stateDao.findById(id);
    }

    @Override
    public State findByAbbr(String abbr) {
        return stateDao.findByAbbr(abbr);
    }

    @Override
    public State findByAbbrOrFullName(String abbr, String name) {
        return stateDao.findByAbbrOrName(abbr, name);
    }


}

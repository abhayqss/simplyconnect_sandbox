package com.scnsoft.exchange.audit.dao;

import com.scnsoft.exchange.audit.model.StateDto;

import java.util.List;

public interface StateDao {
    public List<StateDto> findAll();
}
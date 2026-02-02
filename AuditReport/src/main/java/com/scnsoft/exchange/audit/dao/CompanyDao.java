package com.scnsoft.exchange.audit.dao;

import com.scnsoft.exchange.audit.model.CompanyDto;

import java.util.List;

public interface CompanyDao {
    public List<CompanyDto> findAll();
}
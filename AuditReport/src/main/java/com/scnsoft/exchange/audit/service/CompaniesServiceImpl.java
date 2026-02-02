package com.scnsoft.exchange.audit.service;

import com.scnsoft.exchange.audit.dao.CompanyDao;
import com.scnsoft.exchange.audit.model.CompanyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompaniesServiceImpl implements CompaniesService {

    @Autowired
    private CompanyDao companyDao;

    @Override
    public List<CompanyDto> getCompanies() {
        return companyDao.findAll();
    }
}

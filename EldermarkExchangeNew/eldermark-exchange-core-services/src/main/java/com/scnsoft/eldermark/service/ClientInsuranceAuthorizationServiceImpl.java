package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.ClientInsuranceAuthorizationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class ClientInsuranceAuthorizationServiceImpl implements ClientInsuranceAuthorizationService {

    @Autowired
    private ClientInsuranceAuthorizationDao clientInsuranceAuthorizationDao;

    @Override
    public <P> P findById(Long id, Class<P> projection) {
        return clientInsuranceAuthorizationDao.findById(id, projection).orElse(null);
    }

    @Override
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return clientInsuranceAuthorizationDao.findByIdIn(ids, projection);

    }
}

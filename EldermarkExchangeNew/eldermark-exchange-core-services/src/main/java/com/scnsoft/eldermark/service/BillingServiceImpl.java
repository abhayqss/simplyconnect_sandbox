package com.scnsoft.eldermark.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.ClientHealthPlanDao;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.client.ClientHealthPlan;

@Service
public class BillingServiceImpl implements BillingService {

    @Autowired
    private ClientHealthPlanDao clientHealthPlanDao;

    @Autowired
    private ClientDao clientDao;

    @Override
    @Transactional(readOnly = true)
    public List<ClientHealthPlan> findByClientId(Long clientId) {
        return clientHealthPlanDao.findAllByClient_id(clientId);
    }

    @Override
    public Optional<Client> findOptionalById(Long clientId) {
        return clientDao.findById(clientId);
    }
}

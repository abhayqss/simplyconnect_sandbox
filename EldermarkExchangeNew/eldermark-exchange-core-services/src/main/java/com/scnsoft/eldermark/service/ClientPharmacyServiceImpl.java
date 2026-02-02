package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.PharmacyDao;
import com.scnsoft.eldermark.entity.community.Community;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientPharmacyServiceImpl implements ClientPharmacyService {

    @Autowired
    private ClientService clientService;

    @Autowired
    private PharmacyDao pharmacyDao;

    @Override
    public List<Community> findPharmaciesAsCommunitiesByClientId(Long clientId) {
        return pharmacyDao.listPharmaciesAsCommunity(clientService.findAllMergedClientsIds(clientId));
    }
}

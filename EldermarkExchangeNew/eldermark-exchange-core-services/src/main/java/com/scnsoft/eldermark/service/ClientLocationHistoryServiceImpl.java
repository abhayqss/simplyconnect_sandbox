package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.ClientLocationHistoryDao;
import com.scnsoft.eldermark.dao.specification.ClientLocationHistorySpecificationGenerator;
import com.scnsoft.eldermark.entity.ClientLocationHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class ClientLocationHistoryServiceImpl implements ClientLocationHistoryService {

    @Autowired
    private ClientLocationHistoryDao clientLocationHistoryDao;

    @Autowired
    private ClientLocationHistorySpecificationGenerator clientLocationHistorySpecificationGenerator;

    @Override
    @Transactional(readOnly = true)
    public <P> Page<P> findByClientId(Long clientId, Class<P> projection, Pageable pageable) {
        var byClientId = clientLocationHistorySpecificationGenerator.byClientId(clientId);
        return clientLocationHistoryDao.findAll(byClientId, projection, pageable);
    }

    @Override
    public ClientLocationHistory save(ClientLocationHistory clientLocationHistory) {
        return clientLocationHistoryDao.save(clientLocationHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long locationId, Class<P> projection) {
        return clientLocationHistoryDao.findById(locationId, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return clientLocationHistoryDao.findByIdIn(ids, projection);
    }
}

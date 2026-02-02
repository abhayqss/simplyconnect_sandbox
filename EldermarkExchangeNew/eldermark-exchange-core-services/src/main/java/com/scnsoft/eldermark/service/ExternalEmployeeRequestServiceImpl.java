package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.ExternalEmployeeRequestDao;
import com.scnsoft.eldermark.entity.ExternalEmployeeInboundReferralCommunity;
import com.scnsoft.eldermark.entity.ExternalEmployeeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ExternalEmployeeRequestServiceImpl implements ExternalEmployeeRequestService {
    @Autowired
    private ExternalEmployeeRequestDao externalEmployeeRequestDao;

    @Override
    @Transactional(readOnly = true)
    public ExternalEmployeeRequest findById(Long id) {
        return externalEmployeeRequestDao.findById(id).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public ExternalEmployeeRequest findByToken(String token) {
        return externalEmployeeRequestDao.findByToken(token);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExternalEmployeeRequest> findByExternalEmployeeLoginNameAndCommunityId(String loginName, long communityId) {
        return externalEmployeeRequestDao.findByEmployeeCommunity_Employee_LoginNameAndEmployeeCommunity_CommunityId(loginName, communityId);
    }

    @Override
    public void deleteById(Long id) {
        externalEmployeeRequestDao.deleteById(id);
    }

    @Override
    public void deleteAllByExternalEmployeeLoginNamesAndCommunityId(Collection<String> loginNames, long communityId) {
        externalEmployeeRequestDao.deleteAllByEmployeeCommunity_Employee_LoginNameInAndEmployeeCommunity_CommunityId(loginNames, communityId);
    }

    @Override
    public ExternalEmployeeRequest create(ExternalEmployeeInboundReferralCommunity employeeCommunity) {
        var request = new ExternalEmployeeRequest();
        request.setToken(UUID.randomUUID().toString());
        request.setCreatedDateTime(Instant.now());
        request.setEmployeeCommunity(employeeCommunity);
        return externalEmployeeRequestDao.save(request);
    }
}

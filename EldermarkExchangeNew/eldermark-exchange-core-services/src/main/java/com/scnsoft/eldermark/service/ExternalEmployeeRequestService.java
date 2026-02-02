package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.ExternalEmployeeInboundReferralCommunity;
import com.scnsoft.eldermark.entity.ExternalEmployeeRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public interface ExternalEmployeeRequestService {

    ExternalEmployeeRequest findById(Long id);

    ExternalEmployeeRequest findByToken(String token);

    Optional<ExternalEmployeeRequest> findByExternalEmployeeLoginNameAndCommunityId(String loginName, long communityId);

    void deleteById(Long id);

    void deleteAllByExternalEmployeeLoginNamesAndCommunityId(Collection<String> loginNames, long communityId);

    ExternalEmployeeRequest create(ExternalEmployeeInboundReferralCommunity employeeCommunity);
}

package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.ExternalEmployeeInboundReferralCommunity;
import com.scnsoft.eldermark.entity.community.Community;

import java.util.Collection;
import java.util.List;

public interface ExternalEmployeeInboundReferralCommunityService {

    ExternalEmployeeInboundReferralCommunity create(String loginName, Community community);

    ExternalEmployeeInboundReferralCommunity create(Employee employee, Community community);

    List<ExternalEmployeeInboundReferralCommunity> findAllByCommunityId(long communityId);

    void deleteAll(Collection<ExternalEmployeeInboundReferralCommunity> externalEmployees);

    boolean isCommunitySharedForAnyEmployee(Collection<Long> employeeIds, Long communityId);

    boolean isExternalEmployee(Employee employee);
}

package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.ExternalEmployeeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ExternalEmployeeRequestDao extends JpaRepository<ExternalEmployeeRequest, Long> {

    ExternalEmployeeRequest findByToken(String token);

    Optional<ExternalEmployeeRequest> findByEmployeeCommunity_Employee_LoginNameAndEmployeeCommunity_CommunityId(String loginName, long communityId);

    void deleteAllByEmployeeCommunity_Employee_LoginNameInAndEmployeeCommunity_CommunityId(Collection<String> loginNames, long communityId);
}

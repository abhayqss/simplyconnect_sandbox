package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import org.springframework.data.jpa.domain.Specification;

public interface PointClickCareSpecifications {
    Specification<Client> clientByPccFacilityIdAndPccPatientId(Long pccFacilityId, Long pccPatientId);

    Specification<Client> clientByPccOrgUuidAndPccPatientId(String pccOrgUuid, Long pccPatientId);

    Specification<Organization> orgByPccOrgUuid(String pccOrgUuid);

    Specification<Community> comunityByOrgIdAndPccFacilityId(Long organizationId, Long pccFacilityId);

    Specification<Community> pccCommunities();
}

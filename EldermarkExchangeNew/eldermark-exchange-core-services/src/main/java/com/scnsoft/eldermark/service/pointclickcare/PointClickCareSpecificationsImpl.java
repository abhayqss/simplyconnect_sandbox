package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Organization_;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.Community_;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@Conditional(PccIntegrationOrPatientMatchEnabledCondition.class)
public class PointClickCareSpecificationsImpl implements PointClickCareSpecifications {

    @Override
    public Specification<Client> clientByPccFacilityIdAndPccPatientId(Long pccFacilityId, Long pccPatientId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.join(Client_.community).get(Community_.pccFacilityId), pccFacilityId),
                        criteriaBuilder.equal(root.get(Client_.pccPatientId), pccPatientId)
                );
    }

    @Override
    public Specification<Client> clientByPccOrgUuidAndPccPatientId(String pccOrgUuid, Long pccPatientId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.join(Client_.organization).get(Organization_.pccOrgUuid), pccOrgUuid),
                        criteriaBuilder.equal(root.get(Client_.pccPatientId), pccPatientId)
                );
    }

    @Override
    public Specification<Organization> orgByPccOrgUuid(String pccOrgUuid) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Organization_.pccOrgUuid), pccOrgUuid);
    }

    @Override
    public Specification<Community> comunityByOrgIdAndPccFacilityId(Long organizationId, Long pccFacilityId) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(Community_.organizationId), organizationId),
                        criteriaBuilder.equal(root.get(Community_.pccFacilityId), pccFacilityId)
                );
    }

    @Override
    public Specification<Community> pccCommunities() {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.isNotNull(root.join(Client_.organization).get(Organization_.pccOrgUuid)),
                        criteriaBuilder.isNotNull(root.get(Community_.pccFacilityId))
                );
    }
}

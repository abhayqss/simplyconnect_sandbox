package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dao.ReferralPriorityDao;
import com.scnsoft.eldermark.dao.StateDao;
import com.scnsoft.eldermark.dto.referral.ReferralDto;
import com.scnsoft.eldermark.entity.basic.DisplayableNamedEntity;
import com.scnsoft.eldermark.entity.referral.Referral;
import com.scnsoft.eldermark.entity.referral.ReferralRequest;
import com.scnsoft.eldermark.entity.referral.ReferralStatus;
import com.scnsoft.eldermark.service.*;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class ReferralEntityConverter implements Converter<ReferralDto, Referral> {

    @Autowired
    private ServiceTypeService serviceTypeService;

    @Autowired
    public ReferralService referralService;

    @Autowired
    public CommunityService communityService;

    @Autowired
    public OrganizationService organizationService;

    @Autowired
    public ClientService clientService;

    @Autowired
    private ReferralPriorityDao referralPriorityDao;

    @Autowired
    private StateDao stateDao;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private PartnerNetworkService partnerNetworkService;

    @Override
    public Referral convert(ReferralDto source) {
        if (source.getId() != null) {
            throw new NotImplementedException("Modification of referral is currently not supported");
        }
        var target = new Referral();

        target.setRequestDatetime(Instant.now());
        target.setModifiedDate(Instant.now());
        target.setReferralStatus(ReferralStatus.PENDING);
        target.setPriority(referralPriorityDao.getOne(source.getPriorityId()));

        var services = serviceTypeService.findAllById(source.getServices());

        target.setServices(services);
        target.setServiceName(services.stream()
                .filter(Objects::nonNull)
                .map(DisplayableNamedEntity::getDisplayName)
                .distinct()
                .collect(Collectors.joining(", "))
        );

        target.setReferralInstructions(source.getInstructions());

        target.setFacesheetShared(source.getIsFacesheetShared());
        target.setCcdShared(source.getIsCcdShared());
        target.setServicePlanShared(source.getIsServicePlanShared());

        var clientDto = source.getClient();
        if (clientDto != null) {

            var client = clientService.getById(clientDto.getId());
            target.setClient(client);
            target.setClientLocation(clientDto.getLocation());
            target.setLocationPhone(clientDto.getLocationPhone());

            if (clientDto.getAddress() != null) {
                var address = clientDto.getAddress();
                target.setAddress(address.getStreet());
                target.setCity(address.getCity());
                target.setState(stateDao.getOne(address.getStateId()));
                target.setZipCode(address.getZip());
            }

            target.setInNetworkInsurance(clientDto.getInsuranceNetworkTitle());
        }

        target.setReferringIndividual(source.getPerson());
        target.setRequestingEmployee(loggedUserService.getCurrentEmployee());
        target.setRequestingOrganizationPhone(source.getOrganizationPhone());
        target.setRequestingOrganizationEmail(source.getOrganizationEmail());

        List<ReferralRequest> requests;
        if (source.getMarketplace() != null) {
            var referralMarketplaceDto = source.getMarketplace();
            var request = referralService.createRequest(communityService.get(referralMarketplaceDto.getCommunityId()));
            request.setSharedChannel(referralMarketplaceDto.getSharedChannel());
            request.setSharedFax(referralMarketplaceDto.getSharedFax());
            request.setSharedPhone(referralMarketplaceDto.getSharedPhone());
            request.setSharedFaxComment(referralMarketplaceDto.getSharedFaxComment());
            request.setZoneId(source.getZoneId().getId());
            requests = List.of(request);
        } else {
            if (CollectionUtils.isNotEmpty(source.getSharedCommunityIds())) {
                requests = source.getSharedCommunityIds()
                        .stream()
                        .distinct()
                        .map(id -> referralService.createRequestForNetwork(communityService.get(id), partnerNetworkService.findNetworksWithAllCommunities(source.getReferringCommunityId(), id)))
                        .collect(Collectors.toList());
            } else {
                requests = referralService.createRequestsWithinSystem(communityService.get(source.getReferringCommunityId()), source.getServices());
            }
        }

        requests.forEach(request -> request.setReferral(target));
        target.setReferralRequests(requests);

        target.setMarketplace(source.getMarketplace() != null);

        target.setRequestingCommunity(communityService.get(source.getReferringCommunityId()));
        target.setRequestingCommunityId(source.getReferringCommunityId());

        return target;
    }
}

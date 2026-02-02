package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.security.projection.dto.DocumentSignatureRequestSecurityFieldsAware;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.ClientListItemDto;
import com.scnsoft.eldermark.entity.client.ClientListInfo;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.client.SecuredClientProperty;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestSecurityService;
import com.scnsoft.eldermark.service.security.ClientSecurityService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientListItemDtoConverter implements ListAndItemConverter<ClientListInfo, ClientListItemDto> {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientSecurityService clientSecurityService;

    @Autowired
    private DocumentSignatureRequestSecurityService signatureRequestSecurityService;

    @Override
    public ClientListItemDto convert(ClientListInfo source) {
        var accessibleSecuredProperties = clientSecurityService.getAccessibleSecuredProperties();
        return convert(source, accessibleSecuredProperties);
    }

    @Override
    public <E extends ClientListInfo> List<ClientListItemDto> convertList(List<E> sourceList) {
        var accessibleSecuredProperties = clientSecurityService.getAccessibleSecuredProperties();
        var nullSafeList = CollectionUtils.emptyIfNull(sourceList);
        return nullSafeList.stream().map(e -> convert(e, accessibleSecuredProperties)).collect(Collectors.toList());
    }

    private ClientListItemDto convert(ClientListInfo source, Collection<SecuredClientProperty> accessibleSecuredProperties) {
        var target = convertBaseInfo(source, accessibleSecuredProperties);
        List<ClientListItemDto> mergedClients = clientService.findAllMergedClientsListItems(source.getId()).stream()
                .filter(x -> x != null && !x.getId().equals(source.getId()))
                .map(source1 -> convertBaseInfo(source1, accessibleSecuredProperties)).collect(Collectors.toList());
        target.setMerged(mergedClients);
        target.setCanEdit(clientSecurityService.canEdit(source.getId()));
        target.setCanRequestSignature(signatureRequestSecurityService.canAdd(DocumentSignatureRequestSecurityFieldsAware.of(
                source.getId(),
                DocumentSignatureRequestSecurityService.ANY_TEMPLATE
        )));
        return target;
    }

    private ClientListItemDto convertBaseInfo(ClientListInfo source, Collection<SecuredClientProperty> accessibleSecuredProperties) {
        var target = new ClientListItemDto();
        target.setFullName(CareCoordinationUtils.getFullName(source.getFirstName(), source.getLastName()));
        target.setGender(source.getGenderDisplayName());
        if (accessibleSecuredProperties.contains(SecuredClientProperty.BIRTH_DATE)) {
            target.setBirthDate(DateTimeUtils.formatLocalDate(source.getBirthDate()));
        }
        if (accessibleSecuredProperties.contains(SecuredClientProperty.SSN)) {
            target.setSsnLastFourDigits(source.getSsnLastFourDigits());
        }
        target.setCreatedDate(DateTimeUtils.toEpochMilli(source.getCreatedDate()));
        target.setCommunity(source.getCommunityName());
        target.setCommunityId(source.getCommunityId());
        target.setId(source.getId());
        if (source.getAvatarAvatarName() != null) {
            target.setAvatarId(source.getAvatarId());
        }
        target.setIsActive(source.getActive());
        if (accessibleSecuredProperties.contains(SecuredClientProperty.RISK_SCORE)) {
            target.setRiskScore(source.getRiskScore());
        }
        target.setCanView(clientSecurityService.canView(source.getId())); //todo number is incorrect
        target.setUnit(source.getUnitNumber());
        return target;
    }
}

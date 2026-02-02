package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.beans.ClientAccessType;
import com.scnsoft.eldermark.beans.ClientFilter;
import com.scnsoft.eldermark.beans.ClientStatus;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.ClientLocationHistory;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.Organization_;
import com.scnsoft.eldermark.mobile.dto.client.ClientDto;
import com.scnsoft.eldermark.mobile.dto.client.ClientListItemDto;
import com.scnsoft.eldermark.mobile.dto.client.location.ClientLocationHistoryDto;
import com.scnsoft.eldermark.mobile.dto.client.location.ClientLocationHistoryListItemDto;
import com.scnsoft.eldermark.mobile.filter.MobileClientFilter;
import com.scnsoft.eldermark.mobile.projection.client.MobileClientListInfo;
import com.scnsoft.eldermark.mobile.projection.client.location.ClientLocationHistoryItem;
import com.scnsoft.eldermark.mobile.projection.client.location.ClientLocationHistoryListItem;
import com.scnsoft.eldermark.projection.IsFavouriteEvaluatedAware;
import com.scnsoft.eldermark.service.ClientLocationHistoryService;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientFacadeImpl implements ClientFacade {

    private static final Sort DEFAULT_SORT = Sort.by(
            Sort.Order.desc(IsFavouriteEvaluatedAware.IS_FAVOURITE_PROPERTY_NAME),
            Sort.Order.asc(Client_.ORGANIZATION + "." + Organization_.NAME),
            Sort.Order.asc(Client_.FIRST_NAME),
            Sort.Order.asc(Client_.LAST_NAME));

    @Autowired
    private ClientService clientService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private BiFunction<MobileClientListInfo, PermissionFilter, ClientListItemDto> mobileClientListInfoConverter;

    @Autowired
    private Converter<Client, ClientDto> clientDtoConverter;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ClientLocationHistoryService clientLocationHistoryService;

    @Autowired
    private Converter<ClientLocationHistoryListItem, ClientLocationHistoryListItemDto> clientLocationHistoryListItemDtoConverter;

    @Autowired
    private Converter<ClientLocationHistoryItem, ClientLocationHistoryDto> clientLocationHistoryDtoConverter;

    @Autowired
    private Converter<ClientLocationHistoryDto, ClientLocationHistory> clientLocationHistoryEntityConverter;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canViewList()")
    public Page<ClientListItemDto> find(MobileClientFilter filter, Pageable pageRequest) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var clientFilter = buildClientFilter(filter, loggedUserService.getCurrentEmployeeId(), permissionFilter);

        return clientService.find(
                        clientFilter,
                        PaginationUtils.sortByDefault(pageRequest, DEFAULT_SORT),
                        MobileClientListInfo.class
                )
                .map(listItem -> mobileClientListInfoConverter.apply(listItem, permissionFilter));
    }

    private ClientFilter buildClientFilter(
            MobileClientFilter filter,
            Long currentEmployeeId,
            PermissionFilter permissionFilter
    ) {
        var clientFilter = new ClientFilter();

        clientFilter.setOrganizationId(filter.getOrganizationId());
        clientFilter.setCommunityIds(filter.getCommunityIds());
        clientFilter.setSearchText(filter.getSearchText());
        clientFilter.setFavouriteOfEmployeeIdHint(currentEmployeeId);
        clientFilter.setPermissionFilter(permissionFilter);
        clientFilter.setClientAccessType(ClientAccessType.IN_LIST);

        clientFilter.setRecordStatus(ClientStatus.ACTIVE);

        return clientFilter;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canView(#clientId)")
    public ClientDto findById(@P("clientId") Long clientId) {
        var client = clientService.findById(clientId);
        return clientDtoConverter.convert(client);
    }

    @Override
    public void setFavourite(Long clientId, boolean favourite) {
        var requestedByEmployeeId = loggedUserService.getCurrentEmployeeId();
        clientService.setFavourite(clientId, favourite, requestedByEmployeeId);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientLocationHistorySecurityService.canViewList(#clientId)")
    public Page<ClientLocationHistoryListItemDto> findLocationHistory(@P("clientId") Long clientId, Pageable pageable) {
        return clientLocationHistoryService.findByClientId(clientId, ClientLocationHistoryListItem.class,
                        PaginationUtils.applyEntitySort(pageable, ClientLocationHistoryListItemDto.class))
                .map(clientLocationHistoryListItemDtoConverter::convert);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientLocationHistorySecurityService.canView(#locationId)")
    public ClientLocationHistoryDto findLocationHistoryById(@P("locationId") Long locationId) {
        var location = clientLocationHistoryService.findById(locationId, ClientLocationHistoryItem.class);
        return clientLocationHistoryDtoConverter.convert(location);
    }

    @Override
    @PreAuthorize("@clientLocationHistorySecurityService.canAdd(#clientLocationHistoryDto)")
    public Long reportLocation(@P("clientLocationHistoryDto") ClientLocationHistoryDto clientLocationHistoryDto) {
        var entity = clientLocationHistoryEntityConverter.convert(clientLocationHistoryDto);
        return clientLocationHistoryService.save(entity).getId();
    }

    public static void main(String[] args) throws IOException {
        var claimsFile = new File("04042022_220506_CONSANA_RX_20220403_100101.txt");
        var linesFile = new File("lines.txt");

        var lines = Files.readAllLines(linesFile.toPath());

        var claims = Files.readAllLines(claimsFile.toPath());

        var newClaims = new ArrayList<String>(lines.size());
        newClaims.add(claims.get(0)); //header

        lines.forEach(line -> {
            var cc = claims.stream().filter(c -> c.contains(line)).collect(Collectors.toList());
            if (cc.isEmpty()) {
                throw new RuntimeException("Didn't find for " + line);
            }
            if (cc.size() > 1) {
                throw new RuntimeException("Found more that one for " + line);
            }
            cc.forEach(newClaims::add);
        });

        if (newClaims.size() != lines.size() + 1) {
            throw new RuntimeException("failed size not 91");
        }

        Files.write(new File("claims.txt").toPath(), newClaims);
    }
}

package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.ClientProblemCount;
import com.scnsoft.eldermark.beans.ClientProblemFilter;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.ClientProblemDto;
import com.scnsoft.eldermark.dto.ClientProblemListItemDto;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledValueEntityDto;
import com.scnsoft.eldermark.entity.document.ccd.ClientProblem;
import com.scnsoft.eldermark.entity.document.ccd.ClientProblemStatus;
import com.scnsoft.eldermark.service.ClientProblemService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class ClientProblemFacadeImpl implements ClientProblemFacade {

    @Autowired
    private ListAndItemConverter<ClientProblem, ClientProblemListItemDto> clientProblemListItemDtoConverter;

    @Autowired
    private Converter<ClientProblem, ClientProblemDto> clientProblemDtoConverter;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private ClientProblemService clientProblemService;

    @Override
    @PreAuthorize("@clientProblemSecurityService.canViewList() && " +
            "@clientProblemSecurityService.canViewOfClientIfPresent(#filter)")
    public Page<ClientProblemListItemDto> find(@P("filter") ClientProblemFilter filter, Pageable pageRequest) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return clientProblemService.find(filter, permissionFilter, pageRequest).map(clientProblemListItemDtoConverter::convert);
    }

    @Override
    @PreAuthorize("@clientProblemSecurityService.canView(#id)")
    public ClientProblemDto findById(@P("id") Long id) {
        return clientProblemDtoConverter.convert(clientProblemService.findById(id));
    }

    @Override
    @PreAuthorize("@clientProblemSecurityService.canViewList() && " +
            "@clientProblemSecurityService.canViewOfClientIfPresent(#filter)")
    public List<NamedTitledValueEntityDto<Long>> countGroupedByStatus(@P("filter") ClientProblemFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var statistics = clientProblemService.countGroupedByStatus(filter, permissionFilter);
        return Stream.of(ClientProblemStatus.values())
                .map(clientProblemStatus -> new NamedTitledValueEntityDto<>(clientProblemStatus.name(), clientProblemStatus.getTitle(), findCountByStatus(statistics, clientProblemStatus)))
                .collect(Collectors.toList());
    }

    private Long findCountByStatus(List<ClientProblemCount> statistics, ClientProblemStatus status) {
        return statistics.stream().filter(clientProblemCount -> clientProblemCount.getStatus() == status).findFirst().map(ClientProblemCount::getCount).orElse(0L);
    }
}

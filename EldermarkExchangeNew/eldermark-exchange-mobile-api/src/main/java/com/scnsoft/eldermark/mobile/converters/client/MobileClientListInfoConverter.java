package com.scnsoft.eldermark.mobile.converters.client;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.mobile.dto.client.ClientListItemDto;
import com.scnsoft.eldermark.mobile.projection.client.MobileClientListInfo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.BiFunction;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class MobileClientListInfoConverter extends BaseClientDtoConverter
        implements BiFunction<MobileClientListInfo, PermissionFilter, ClientListItemDto> {

    @Override
    public ClientListItemDto apply(MobileClientListInfo mobileClientListInfo, PermissionFilter permissionFilter) {
        var dto = new ClientListItemDto();
        fillListItem(mobileClientListInfo, dto, permissionFilter);
        return dto;
    }
}

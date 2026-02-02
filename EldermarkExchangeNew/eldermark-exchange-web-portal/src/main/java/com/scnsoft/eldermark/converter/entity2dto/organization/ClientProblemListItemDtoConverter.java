package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.entity.document.ccd.ClientProblem;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.stereotype.Component;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.ClientProblemListItemDto;

@Component
public class ClientProblemListItemDtoConverter implements ListAndItemConverter<ClientProblem, ClientProblemListItemDto> {

    @Override
    public ClientProblemListItemDto convert(ClientProblem source) {
        ClientProblemListItemDto target = new ClientProblemListItemDto();
        target.setId(source.getId());
        target.setName(source.getProblem());
        target.setIdentifiedDate(DateTimeUtils.toEpochMilli(source.getIdentifiedDate()));
        target.setResolvedDate(DateTimeUtils.toEpochMilli(source.getStoppedDate()));
        target.setCode(source.getProblemCode());
        target.setCodeSet(source.getProblemCodeSet());
        return target;
    }

}

package com.scnsoft.eldermark.converter.entity2dto.client.expense;

import com.scnsoft.eldermark.dto.client.expense.ClientExpenseListItemDto;
import com.scnsoft.eldermark.entity.client.expense.ClientExpenseListItemDetailsAware;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClientExpenseListItemConverter implements Converter<ClientExpenseListItemDetailsAware, ClientExpenseListItemDto> {

    @Override
    public ClientExpenseListItemDto convert(ClientExpenseListItemDetailsAware source) {

        var target = new ClientExpenseListItemDto();

        target.setId(source.getId());
        target.setCost(source.getCost());
        target.setAuthor(source.getAuthorFullName());
        target.setDate(DateTimeUtils.toEpochMilli(source.getDate()));
        target.setReportedDate(DateTimeUtils.toEpochMilli(source.getReportedDate()));
        target.setTypeName(source.getType().name());
        target.setTypeTitle(source.getType().getDisplayName());

        return target;
    }
}

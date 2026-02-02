package com.scnsoft.eldermark.converter.entity2dto.client.expense;

import com.scnsoft.eldermark.dto.client.expense.ClientExpenseDto;
import com.scnsoft.eldermark.entity.client.expense.ClientExpense;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class ClientExpenseEntityConverter implements Converter<ClientExpense, ClientExpenseDto> {

    @Override
    public ClientExpenseDto convert(ClientExpense source) {

        var target = new ClientExpenseDto();

        target.setId(source.getId());
        target.setClientId(source.getClientId());
        target.setComment(source.getComment());
        target.setCost(source.getCost());
        target.setAuthor(source.getAuthor().getFullName());
        target.setDate(DateTimeUtils.toEpochMilli(source.getDate()));
        target.setReportedDate(DateTimeUtils.toEpochMilli(source.getReportedDate()));
        target.setTypeName(source.getType());
        target.setTypeTitle(source.getType().getDisplayName());
        target.setCumulativeCost(source.getCumulativeCost());

        return target;
    }
}

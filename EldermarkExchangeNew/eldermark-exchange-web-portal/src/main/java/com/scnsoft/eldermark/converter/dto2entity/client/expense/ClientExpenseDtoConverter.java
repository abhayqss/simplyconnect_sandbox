package com.scnsoft.eldermark.converter.dto2entity.client.expense;

import com.scnsoft.eldermark.dto.client.expense.ClientExpenseDto;
import com.scnsoft.eldermark.entity.client.expense.ClientExpense;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class ClientExpenseDtoConverter implements Converter<ClientExpenseDto, ClientExpense> {

    @Autowired
    private ClientService clientService;

    @Override
    public ClientExpense convert(ClientExpenseDto source) {

        var target = new ClientExpense();

        target.setClient(clientService.findById(source.getClientId()));
        target.setClientId(source.getClientId());

        target.setComment(source.getComment());
        target.setCost(source.getCost());
        target.setDate(DateTimeUtils.toInstant(source.getDate()));
        target.setType(source.getTypeName());

        return target;
    }
}

package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.ClientEssentialsDto;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class ClientEssentialsEntityConverter<T extends ClientEssentialsDto> implements Converter<T, Client> {

    @Autowired
    private ClientService clientService;

    @Override
    public Client convert(T source) {
        Client target = getClientEntityWithNonEditable(source);
        updateEditable(source, target);
        return target;
    }


    protected Client getClientEntityWithNonEditable(T source) {
        return clientService.findById(source.getId());
    }

    protected void updateEditable(T source, Client target) {
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setBirthDate(DateTimeUtils.parseDateToLocalDate(source.getBirthDate()));
    }
}

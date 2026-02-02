package com.scnsoft.eldermark.mobile.converters.client.location;

import com.scnsoft.eldermark.entity.ClientLocationHistory;
import com.scnsoft.eldermark.mobile.dto.client.location.ClientLocationHistoryDto;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ClientLocationHistoryEntityConverter
        implements Converter<ClientLocationHistoryDto, ClientLocationHistory> {

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ClientService clientService;

    @Override
    public ClientLocationHistory convert(ClientLocationHistoryDto source) {
        var target = new ClientLocationHistory();

        target.setClientId(source.getClientId());
        target.setClient(clientService.getById(source.getClientId()));

        target.setRecordDatetime(Instant.now());
        target.setSeenDatetime(DateTimeUtils.toInstant(source.getSeenDatetime()));

        target.setReportedById(loggedUserService.getCurrentEmployeeId());
        target.setReportedBy(loggedUserService.getCurrentEmployee());

        target.setLongitude(source.getLongitude());
        target.setLatitude(source.getLatitude());

        return target;
    }
}

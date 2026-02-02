package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dto.pointclickcare.projection.PccClientMatchProjection;
import com.scnsoft.eldermark.dto.pointclickcare.projection.PccClientMatchProjectionAdapter;
import com.scnsoft.eldermark.entity.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(value = "pcc.patientMatch.enabled", havingValue = "true")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class PccClientToClientMatchProjectionConverter implements Converter<Client, PccClientMatchProjection> {


    @Override
    public PccClientMatchProjection convert(Client client) {
        if (client == null) {
            return null;
        }
        return new PccClientMatchProjectionAdapter(client);
    }
}

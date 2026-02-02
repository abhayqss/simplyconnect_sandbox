package com.scnsoft.eldermark.service.report.converter;

import com.scnsoft.eldermark.beans.projection.ClientIntakeDetailsAware;
import com.scnsoft.eldermark.entity.client.report.ClientIntakesReportItem;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClientIntakesConverter implements Converter<ClientIntakeDetailsAware, ClientIntakesReportItem> {

    @Override
    public ClientIntakesReportItem convert(ClientIntakeDetailsAware source) {

        var target = new ClientIntakesReportItem(
            source.getId(), source.getFirstName(), source.getLastName(), source.getCommunityId(),
            source.getCommunityName(), source.getIntakeDate(), source.getLastUpdated(), source.getActive(),
            source.getBirthDate(), source.getCreatedDate(), source.getGenderDisplayName(),
            source.getRaceDisplayName(), null, source.getInNetworkInsuranceDisplayName(), source.getInsurancePlan(),
            source.getExitDate(), source.getActivationDate(), source.getDeactivationDate(), source.getComment(),
            source.getExitComment(), source.getDeactivationReason()
        );

        if (CollectionUtils.isNotEmpty(source.getPerson().getAddresses())) {
            target.setCity(source.getPerson().getAddresses().get(0).getCity());
        }
        return target;
    }

}

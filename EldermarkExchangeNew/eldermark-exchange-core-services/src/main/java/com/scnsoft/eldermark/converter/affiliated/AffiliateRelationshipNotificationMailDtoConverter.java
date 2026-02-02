package com.scnsoft.eldermark.converter.affiliated;

import com.scnsoft.eldermark.dto.notification.affiliated.AffiliatedRelationshipNotificationMailDto;
import com.scnsoft.eldermark.entity.AffiliatedRelationshipNotification;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AffiliateRelationshipNotificationMailDtoConverter implements Converter<AffiliatedRelationshipNotification, AffiliatedRelationshipNotificationMailDto> {

    @Override
    public AffiliatedRelationshipNotificationMailDto convert(AffiliatedRelationshipNotification source) {
        var target = new AffiliatedRelationshipNotificationMailDto();
        target.setPrimaryOrganizationName(source.getPrimaryOrganization().getName());
        target.setAffiliatedOrganizationName(source.getAffiliatedOrganization().getName());
        target.setReceiverFullName(source.getReceiver().getFullName());
        target.setReceiverEmail(source.getDestination());
        target.setAuthorFullName(source.getAuthor().getFullName());
        target.setTerminated(source.isTerminated());
        return target;
    }
}

package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.PartnerNetworkFilter;
import com.scnsoft.eldermark.dto.PartnerNetworkOrganizationListItemDto;

import java.util.Collection;

public interface PartnerNetworkFacade {

    Collection<PartnerNetworkOrganizationListItemDto> findAllGroupedByOrganization(PartnerNetworkFilter filter);
}

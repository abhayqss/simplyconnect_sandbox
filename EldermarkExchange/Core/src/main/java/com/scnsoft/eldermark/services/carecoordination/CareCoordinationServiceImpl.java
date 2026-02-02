package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.shared.carecoordination.ManageOrgPanelStateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by averazub on 4/21/2016.
 */
@Service
public class CareCoordinationServiceImpl implements CareCoordinationService {

    @Autowired
    OrganizationService organizationService;

    @Override
    public ManageOrgPanelStateDto getManageOrgPanelState() {
        return null;
    }
}

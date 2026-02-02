package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.entity.BasicEntity;
import com.scnsoft.eldermark.services.cda.templates.MultiHeaderFactory;
import com.scnsoft.eldermark.services.cda.templates.ParsableMultiHeader;
import org.eclipse.mdht.uml.cda.Authorization;

public interface AuthorizationFactory extends
        MultiHeaderFactory<Authorization, BasicEntity>,
        ParsableMultiHeader<Authorization, BasicEntity> {
}

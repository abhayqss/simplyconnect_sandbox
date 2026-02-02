package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import org.eclipse.mdht.uml.cda.Authorization;

public interface AuthorizationFactory extends
        MultiHeaderFactory<Authorization, BasicEntity>,
        ParsableMultiHeader<Authorization, BasicEntity> {
}

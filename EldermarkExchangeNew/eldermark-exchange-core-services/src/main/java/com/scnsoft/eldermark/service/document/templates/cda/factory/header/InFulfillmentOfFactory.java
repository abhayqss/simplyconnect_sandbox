package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import org.eclipse.mdht.uml.cda.InFulfillmentOf;

public interface InFulfillmentOfFactory extends
        MultiHeaderFactory<InFulfillmentOf, BasicEntity>,
        ParsableMultiHeader<InFulfillmentOf, BasicEntity> {
}

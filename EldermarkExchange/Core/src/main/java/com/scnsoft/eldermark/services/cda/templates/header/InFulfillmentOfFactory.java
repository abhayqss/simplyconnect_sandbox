package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.entity.BasicEntity;
import com.scnsoft.eldermark.services.cda.templates.MultiHeaderFactory;
import com.scnsoft.eldermark.services.cda.templates.ParsableMultiHeader;
import org.eclipse.mdht.uml.cda.InFulfillmentOf;

public interface InFulfillmentOfFactory extends
        MultiHeaderFactory<InFulfillmentOf, BasicEntity>,
        ParsableMultiHeader<InFulfillmentOf, BasicEntity> {
}

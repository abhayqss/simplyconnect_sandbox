package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.services.cda.templates.MultiHeaderFactory;
import com.scnsoft.eldermark.services.cda.templates.ParsableMultiHeader;

public interface InformationRecipientFactory extends
        MultiHeaderFactory<org.eclipse.mdht.uml.cda.InformationRecipient, com.scnsoft.eldermark.entity.InformationRecipient>,
        ParsableMultiHeader<org.eclipse.mdht.uml.cda.InformationRecipient, com.scnsoft.eldermark.entity.InformationRecipient> {
}

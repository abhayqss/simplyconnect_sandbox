package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.services.cda.templates.ParsableSingleHeader;
import com.scnsoft.eldermark.services.cda.templates.SingleHeaderFactory;
import org.eclipse.mdht.uml.cda.DataEnterer;

/**
 * ClinicalDocument (C-CDA) MAY contain zero or one [0..1] dataEnterer (CONF:5441, CONF:1198-28678).
 */
public interface DataEntererFactory extends
        SingleHeaderFactory<DataEnterer, com.scnsoft.eldermark.entity.DataEnterer>,
        ParsableSingleHeader<DataEnterer, com.scnsoft.eldermark.entity.DataEnterer> {
}

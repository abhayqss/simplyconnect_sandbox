package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.SingleHeaderFactory;
import org.eclipse.mdht.uml.cda.DataEnterer;

/**
 * ClinicalDocument (C-CDA) MAY contain zero or one [0..1] dataEnterer (CONF:5441, CONF:1198-28678).
 */
public interface DataEntererFactory extends
        SingleHeaderFactory<DataEnterer, com.scnsoft.eldermark.entity.document.ccd.DataEnterer>,
        ParsableSingleHeader<DataEnterer, com.scnsoft.eldermark.entity.document.ccd.DataEnterer> {
}

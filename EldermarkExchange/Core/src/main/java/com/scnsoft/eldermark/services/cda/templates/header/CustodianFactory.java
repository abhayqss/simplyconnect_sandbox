package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.services.cda.templates.ParsableSingleHeader;
import com.scnsoft.eldermark.services.cda.templates.SingleHeaderFactory;
import org.eclipse.mdht.uml.cda.Custodian;

/**
 * ClinicalDocument (C-CDA) SHALL contain exactly one [1..1] custodian (CONF:5519, CONF:1198-5519).
 */
public interface CustodianFactory extends
        SingleHeaderFactory<Custodian, com.scnsoft.eldermark.entity.Custodian>,
        ParsableSingleHeader<Custodian, com.scnsoft.eldermark.entity.Custodian> {
}

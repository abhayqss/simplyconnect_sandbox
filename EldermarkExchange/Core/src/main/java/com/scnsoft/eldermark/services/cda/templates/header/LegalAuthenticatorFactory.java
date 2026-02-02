package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.services.cda.templates.ParsableSingleHeader;
import com.scnsoft.eldermark.services.cda.templates.SingleHeaderFactory;

/**
 * ClinicalDocument (C-CDA) SHOULD contain zero or one [0..1] legalAuthenticator (CONF:5579, CONF:1198-5579).
 */
public interface LegalAuthenticatorFactory extends
        SingleHeaderFactory<org.eclipse.mdht.uml.cda.LegalAuthenticator, com.scnsoft.eldermark.entity.LegalAuthenticator>,
        ParsableSingleHeader<org.eclipse.mdht.uml.cda.LegalAuthenticator, com.scnsoft.eldermark.entity.LegalAuthenticator> {
}

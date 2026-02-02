package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.SingleHeaderFactory;

/**
 * ClinicalDocument (C-CDA) SHOULD contain zero or one [0..1] legalAuthenticator (CONF:5579, CONF:1198-5579).
 */
public interface LegalAuthenticatorFactory extends
        SingleHeaderFactory<org.eclipse.mdht.uml.cda.LegalAuthenticator, com.scnsoft.eldermark.entity.document.ccd.LegalAuthenticator>,
        ParsableSingleHeader<org.eclipse.mdht.uml.cda.LegalAuthenticator, com.scnsoft.eldermark.entity.document.ccd.LegalAuthenticator> {
}

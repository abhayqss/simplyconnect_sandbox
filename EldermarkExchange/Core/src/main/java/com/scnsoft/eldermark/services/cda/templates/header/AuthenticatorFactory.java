package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.services.cda.templates.MultiHeaderFactory;
import com.scnsoft.eldermark.services.cda.templates.ParsableMultiHeader;

public interface AuthenticatorFactory extends
        MultiHeaderFactory<org.eclipse.mdht.uml.cda.Authenticator, com.scnsoft.eldermark.entity.Authenticator>,
        ParsableMultiHeader<org.eclipse.mdht.uml.cda.Authenticator, com.scnsoft.eldermark.entity.Authenticator> {
}

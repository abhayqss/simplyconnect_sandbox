package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.LegalAuthenticator;

public interface LegalAuthenticatorDao extends ResidentAwareDao<LegalAuthenticator> {
    LegalAuthenticator getCcdLegalAuthenticator(Long residentId);
}

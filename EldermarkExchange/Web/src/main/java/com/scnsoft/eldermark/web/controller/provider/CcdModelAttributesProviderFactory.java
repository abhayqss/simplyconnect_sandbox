package com.scnsoft.eldermark.web.controller.provider;

import com.google.common.base.Optional;

public interface CcdModelAttributesProviderFactory {

    Optional<CcdModelAttributesProvider> getModelAttributesProvider(String sectionName);

}

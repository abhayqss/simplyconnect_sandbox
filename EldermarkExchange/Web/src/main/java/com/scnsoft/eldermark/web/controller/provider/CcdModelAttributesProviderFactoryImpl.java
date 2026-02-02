package com.scnsoft.eldermark.web.controller.provider;

import com.google.common.base.Optional;
import com.scnsoft.eldermark.entity.CcdSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CcdModelAttributesProviderFactoryImpl implements CcdModelAttributesProviderFactory {

    @Autowired
    List<CcdModelAttributesProvider> modelAttributesProviderList;

    @Override
    public Optional<CcdModelAttributesProvider> getModelAttributesProvider(String sectionName) {
        final Optional<CcdSection> section = CcdSection.loadByName(sectionName);
        if (!section.isPresent()) {
            return Optional.absent();
        }
        for (CcdModelAttributesProvider provider: modelAttributesProviderList) {
            if (provider.getSection().equals(section.get())) {
                return Optional.of(provider);
            }
        }
        return Optional.absent();
    }
}

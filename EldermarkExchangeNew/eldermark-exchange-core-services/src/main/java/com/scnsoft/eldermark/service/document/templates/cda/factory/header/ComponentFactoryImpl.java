package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.Component2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

// Stub
@Component
public class ComponentFactoryImpl extends OptionalTemplateFactory implements ComponentFactory {

    @Value("${header.component.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    @Override
    public Component2 buildTemplateInstance(BasicEntity component) {
        return CDAFactory.eINSTANCE.createComponent2();
    }

    @Override
    public BasicEntity parseSection(Client client, Component2 component) {
        if (!CcdParseUtils.hasContent(component)) {
            return null;
        }
        checkNotNull(client);
        // TODO implement
        return null;
    }

}

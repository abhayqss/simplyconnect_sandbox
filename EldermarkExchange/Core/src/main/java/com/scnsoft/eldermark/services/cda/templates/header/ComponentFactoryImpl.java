package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.entity.BasicEntity;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.OptionalTemplateFactory;
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
    public BasicEntity parseSection(Resident resident, Component2 component) {
        if (!CcdParseUtils.hasContent(component)) {
            return null;
        }
        checkNotNull(resident);
        // TODO implement
        return null;
    }

}

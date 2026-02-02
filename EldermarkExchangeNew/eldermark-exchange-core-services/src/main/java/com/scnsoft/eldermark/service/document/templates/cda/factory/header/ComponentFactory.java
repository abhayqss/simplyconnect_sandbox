package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.SingleHeaderFactory;
import org.eclipse.mdht.uml.cda.Component2;

public interface ComponentFactory extends
        SingleHeaderFactory<Component2, BasicEntity>,
        ParsableSingleHeader<Component2, BasicEntity> {
}

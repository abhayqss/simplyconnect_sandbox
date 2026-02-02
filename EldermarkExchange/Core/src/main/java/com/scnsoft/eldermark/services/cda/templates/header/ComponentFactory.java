package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.entity.BasicEntity;
import com.scnsoft.eldermark.services.cda.templates.ParsableSingleHeader;
import com.scnsoft.eldermark.services.cda.templates.SingleHeaderFactory;
import org.eclipse.mdht.uml.cda.Component2;

public interface ComponentFactory extends
        SingleHeaderFactory<Component2, BasicEntity>,
        ParsableSingleHeader<Component2, BasicEntity> {
}

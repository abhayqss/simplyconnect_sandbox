package com.scnsoft.eldermark.services.cda.templates;

import com.scnsoft.eldermark.entity.BasicEntity;
import org.eclipse.mdht.uml.hl7.rim.InfrastructureRoot;

public interface SingleHeaderFactory<H extends InfrastructureRoot, D extends BasicEntity> extends TemplateFactory<H, D> {
}

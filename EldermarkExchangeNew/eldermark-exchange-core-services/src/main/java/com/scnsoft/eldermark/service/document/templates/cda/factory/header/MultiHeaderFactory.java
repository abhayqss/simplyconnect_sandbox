package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.TemplateFactory;
import org.eclipse.mdht.uml.hl7.rim.InfrastructureRoot;

import java.util.Collection;

public interface MultiHeaderFactory<H extends InfrastructureRoot, D extends BasicEntity> extends TemplateFactory<Collection<H>, Collection<D>> {
}

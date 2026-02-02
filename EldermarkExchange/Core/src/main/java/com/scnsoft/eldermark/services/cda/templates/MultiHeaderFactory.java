package com.scnsoft.eldermark.services.cda.templates;

import com.scnsoft.eldermark.entity.BasicEntity;
import org.eclipse.mdht.uml.hl7.rim.InfrastructureRoot;

import java.util.Collection;

public interface MultiHeaderFactory<H extends InfrastructureRoot, D extends BasicEntity> extends TemplateFactory<Collection<H>, Collection<D>> {
}

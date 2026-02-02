package com.scnsoft.eldermark.service.document.templates.cda.factory.sections;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import org.eclipse.mdht.uml.cda.Section;

import java.util.Collection;

public interface SectionFactory<S extends Section, D extends BasicEntity> extends TemplateFactory<S, Collection<D>> {
}

package com.scnsoft.eldermark.services.cda.templates;

import com.scnsoft.eldermark.entity.BasicEntity;
import org.eclipse.mdht.uml.cda.Section;

import java.util.Collection;

public interface SectionFactory<S extends Section, D extends BasicEntity> extends TemplateFactory<S, Collection<D>> {
}

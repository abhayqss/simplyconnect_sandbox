package com.scnsoft.eldermark.service.document.templates.cda.parser.sections;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.SectionFactory;
import org.eclipse.mdht.uml.cda.Section;

public interface ParsableSectionFactory<S extends Section, D extends BasicEntity> extends ParsableSection<S, D>, SectionFactory<S, D> {
}

package com.scnsoft.eldermark.services.cda.templates;

import com.scnsoft.eldermark.entity.BasicEntity;
import org.eclipse.mdht.uml.cda.Section;

public interface ParsableSectionFactory<S extends Section, D extends BasicEntity> extends ParsableSection<S, D>, SectionFactory<S, D> {
}

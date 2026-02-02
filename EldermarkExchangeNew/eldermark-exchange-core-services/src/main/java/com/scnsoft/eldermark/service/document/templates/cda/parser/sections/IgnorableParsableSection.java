package com.scnsoft.eldermark.service.document.templates.cda.parser.sections;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import org.eclipse.mdht.uml.cda.Section;

public interface IgnorableParsableSection<S extends Section, D extends BasicEntity> extends ParsableSection<S, D> {
    default boolean isSectionIgnored(S section) {
        return false;
    }
}

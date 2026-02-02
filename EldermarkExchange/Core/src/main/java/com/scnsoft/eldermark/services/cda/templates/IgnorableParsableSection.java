package com.scnsoft.eldermark.services.cda.templates;

import com.scnsoft.eldermark.entity.BasicEntity;
import org.eclipse.mdht.uml.cda.Section;
import org.eclipse.mdht.uml.hl7.rim.InfrastructureRoot;

public interface IgnorableParsableSection<S extends Section, D extends BasicEntity> extends ParsableSection<S, D> {
    boolean isSectionIgnored(S section);
}

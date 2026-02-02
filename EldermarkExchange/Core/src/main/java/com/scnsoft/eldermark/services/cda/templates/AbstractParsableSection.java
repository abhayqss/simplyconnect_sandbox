package com.scnsoft.eldermark.services.cda.templates;

import com.scnsoft.eldermark.entity.BasicEntity;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.IgnorableParsableEntry;
import org.eclipse.mdht.uml.cda.Section;
import org.eclipse.mdht.uml.hl7.rim.InfrastructureRoot;

import java.util.Collections;
import java.util.List;

public abstract class AbstractParsableSection<E extends InfrastructureRoot, S extends Section, D extends BasicEntity>
        extends AbstractIgnorableParsableEntry<E>
        implements IgnorableParsableSection<S, D>, IgnorableParsableEntry<E> {

    @Override
    public boolean isSectionIgnored(S section) {
        return false;
    }

    public abstract List<D> doParseSection(Resident resident, S section);

    @Override
    public List<D> parseSection(Resident resident, S section) {
        if (isSectionIgnored(section)) {
            return Collections.emptyList();
        } else {
            return doParseSection(resident, section);
        }
    }
}

package com.scnsoft.eldermark.services.cda.templates;

import com.scnsoft.eldermark.services.cda.templates.sections.entries.IgnorableParsableEntry;
import org.eclipse.mdht.uml.hl7.rim.InfrastructureRoot;

public abstract class AbstractIgnorableParsableEntry<E extends InfrastructureRoot> implements IgnorableParsableEntry<E> {
    @Override
    public boolean isEntryIgnored(E entry) {
        return false;
    }
}

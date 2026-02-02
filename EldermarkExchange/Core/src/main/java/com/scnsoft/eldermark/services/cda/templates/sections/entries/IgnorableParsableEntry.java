package com.scnsoft.eldermark.services.cda.templates.sections.entries;

import org.eclipse.mdht.uml.hl7.rim.InfrastructureRoot;

public interface IgnorableParsableEntry<E extends InfrastructureRoot> {
    boolean isEntryIgnored(E entry);
}

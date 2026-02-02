package com.scnsoft.eldermark.service.document.templates.cda.parser.entries;

import org.eclipse.mdht.uml.hl7.rim.InfrastructureRoot;

public interface IgnorableParsableEntry<E extends InfrastructureRoot> {

    default boolean isEntryIgnored(E entry) {
        return false;
    }
}

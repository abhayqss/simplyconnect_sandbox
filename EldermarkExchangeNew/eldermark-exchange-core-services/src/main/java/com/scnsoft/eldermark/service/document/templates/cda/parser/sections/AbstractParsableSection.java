package com.scnsoft.eldermark.service.document.templates.cda.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.IgnorableParsableEntry;
import org.eclipse.mdht.uml.cda.Section;
import org.eclipse.mdht.uml.hl7.rim.InfrastructureRoot;

import java.util.Collections;
import java.util.List;

public abstract class AbstractParsableSection<E extends InfrastructureRoot, S extends Section, D extends BasicEntity>
        implements IgnorableParsableSection<S, D>, IgnorableParsableEntry<E> {

    public abstract List<D> doParseSection(Client resident, S section);

    @Override
    public List<D> parseSection(Client client, S section) {
        if (isSectionIgnored(section)) {
            return Collections.emptyList();
        } else {
            return doParseSection(client, section);
        }
    }
}

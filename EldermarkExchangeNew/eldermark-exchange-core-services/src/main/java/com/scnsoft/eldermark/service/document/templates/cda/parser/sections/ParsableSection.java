package com.scnsoft.eldermark.service.document.templates.cda.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import org.eclipse.mdht.uml.cda.Section;

import java.util.Collection;

public interface ParsableSection<S extends Section, D extends BasicEntity> {

    Collection<D> parseSection(Client client, S section);

}

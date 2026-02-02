package com.scnsoft.eldermark.services.cda.templates;

import com.scnsoft.eldermark.entity.BasicEntity;
import com.scnsoft.eldermark.entity.Resident;
import org.eclipse.mdht.uml.cda.Section;

import java.util.Collection;

public interface ParsableSection<S extends Section, D extends BasicEntity> {

    Collection<D> parseSection(Resident resident, S section);

}

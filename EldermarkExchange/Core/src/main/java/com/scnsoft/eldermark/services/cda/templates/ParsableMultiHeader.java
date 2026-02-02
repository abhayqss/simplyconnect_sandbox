package com.scnsoft.eldermark.services.cda.templates;

import com.scnsoft.eldermark.entity.BasicEntity;
import org.eclipse.mdht.uml.hl7.rim.InfrastructureRoot;

import java.util.Collection;
import java.util.List;

public interface ParsableMultiHeader<H extends InfrastructureRoot, D extends BasicEntity> extends ParsableHeader<Collection<H>, List<D>> {
}

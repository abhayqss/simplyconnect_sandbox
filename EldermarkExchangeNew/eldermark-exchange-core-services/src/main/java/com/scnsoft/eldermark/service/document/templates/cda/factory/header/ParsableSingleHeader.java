package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import org.eclipse.mdht.uml.hl7.rim.InfrastructureRoot;

public interface ParsableSingleHeader<H extends InfrastructureRoot, D extends BasicEntity> extends ParsableHeader<H, D> {
}

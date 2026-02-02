package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.document.ccd.Informant;
import org.eclipse.mdht.uml.cda.Informant12;

public interface InformantFactory extends
        MultiHeaderFactory<Informant12, Informant>,
        ParsableMultiHeader<Informant12, Informant> {
}

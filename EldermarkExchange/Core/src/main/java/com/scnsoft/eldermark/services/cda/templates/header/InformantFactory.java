package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.entity.Informant;
import com.scnsoft.eldermark.services.cda.templates.MultiHeaderFactory;
import com.scnsoft.eldermark.services.cda.templates.ParsableMultiHeader;
import org.eclipse.mdht.uml.cda.Informant12;

public interface InformantFactory extends
        MultiHeaderFactory<Informant12, Informant>,
        ParsableMultiHeader<Informant12, Informant> {
}

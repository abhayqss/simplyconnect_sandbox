package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.entity.Participant;
import com.scnsoft.eldermark.services.cda.templates.MultiHeaderFactory;
import com.scnsoft.eldermark.services.cda.templates.ParsableMultiHeader;
import org.eclipse.mdht.uml.cda.Participant1;

public interface ParticipantFactory extends
        MultiHeaderFactory<Participant1, Participant>,
        ParsableMultiHeader<Participant1, Participant> {
}

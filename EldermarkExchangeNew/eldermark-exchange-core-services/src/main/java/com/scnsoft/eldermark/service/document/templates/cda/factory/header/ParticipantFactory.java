package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.document.ccd.Participant;
import org.eclipse.mdht.uml.cda.Participant1;

public interface ParticipantFactory extends
        MultiHeaderFactory<Participant1, Participant>,
        ParsableMultiHeader<Participant1, Participant> {
}

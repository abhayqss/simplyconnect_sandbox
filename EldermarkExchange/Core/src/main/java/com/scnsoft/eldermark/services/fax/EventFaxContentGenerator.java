package com.scnsoft.eldermark.services.fax;

import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import org.springframework.stereotype.Service;

@Service()
public interface EventFaxContentGenerator extends FaxContentGenerator<EventDto> {

}

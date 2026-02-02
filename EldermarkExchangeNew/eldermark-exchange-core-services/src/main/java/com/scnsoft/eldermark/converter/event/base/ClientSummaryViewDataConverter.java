package com.scnsoft.eldermark.converter.event.base;

import com.scnsoft.eldermark.dto.event.ClientSummaryViewData;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.event.Event;

public interface ClientSummaryViewDataConverter<C extends ClientSummaryViewData>  {

    C convert(Client c);

    C convert(Event e);

}

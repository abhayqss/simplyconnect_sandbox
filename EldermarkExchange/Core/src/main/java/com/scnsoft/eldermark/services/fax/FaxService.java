package com.scnsoft.eldermark.services.fax;

import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import com.scnsoft.eldermark.shared.carecoordination.service.FaxDto;

import java.util.concurrent.Future;

/**
 * Created by pzhurba on 09-Dec-15.
 */
public interface FaxService {

    <T> Future<Boolean> sendFax(FaxDto faxDto, T dto, FaxContentGenerator<T> faxContentGenerator);
}

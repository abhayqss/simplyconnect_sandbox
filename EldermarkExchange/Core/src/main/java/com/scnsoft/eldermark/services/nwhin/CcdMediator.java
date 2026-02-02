package com.scnsoft.eldermark.services.nwhin;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.shared.ResidentDto;

/**
 * TODO refactoring: rename to NwhinCcdService and move to another package
 * @author phomal
 * Created on 2/13/2017.
 */
public interface CcdMediator {

    String processCcd(String homeCommunityId, ExchangeUserDetails userDetails, ResidentDto resident) throws Exception;

}

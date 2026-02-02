package com.scnsoft.eldermark.services.inbound.therap.idf;

import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.inbound.therap.csv.TherapIdfCSV;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;

public interface TherapIdfService {

    /**
     *  find, create and update must happen in single new transaction - therefore we need this method and need to
     *  know which action was perfomed (create or update)
     * @param therapIdfCSV
     * @param idfCommunityId
     * @return pair of resident and 'already Existed' (resident was found - update action) flag
     */
    Pair<Resident, Boolean> createOrUpdateResident(TherapIdfCSV therapIdfCSV, Long idfCommunityId);
}

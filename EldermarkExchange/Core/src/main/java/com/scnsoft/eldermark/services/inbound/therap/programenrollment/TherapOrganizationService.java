package com.scnsoft.eldermark.services.inbound.therap.programenrollment;

import com.scnsoft.eldermark.entity.inbound.therap.csv.TherapProgramEnrollmentCsv;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface TherapOrganizationService {

    /**
     * Find and create need runs in single new transaction.
     *
     * @return organization and 'alreadyExisted' flag.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Pair<Long, Boolean> findOrCreateOrganization(TherapProgramEnrollmentCsv enrollmentCsv);

}

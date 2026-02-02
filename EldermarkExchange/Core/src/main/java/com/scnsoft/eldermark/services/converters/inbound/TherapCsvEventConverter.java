package com.scnsoft.eldermark.services.converters.inbound;

import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.entity.inbound.therap.csv.TherapEventCSV;
import com.scnsoft.eldermark.schema.Community;
import com.scnsoft.eldermark.schema.Organization;
import com.scnsoft.eldermark.schema.Patient;

public interface TherapCsvEventConverter {

    Organization convertToOrganizationSchema(TherapEventCSV eventCSV);

    Community convertToCommunitySchema(TherapEventCSV eventCSV);

    Patient convertToPatientSchema(TherapEventCSV eventCSV);

    Event convertToEvent(TherapEventCSV eventCSV, CareCoordinationResident resident);

}

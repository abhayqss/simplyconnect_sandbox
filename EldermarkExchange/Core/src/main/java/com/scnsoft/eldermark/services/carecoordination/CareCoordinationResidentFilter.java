package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.schema.Patient;

import java.util.Date;

/**
 * Created by pzhurba on 13-Nov-15.
 */
public class CareCoordinationResidentFilter {
    private final Patient patient;
    public CareCoordinationResidentFilter(final Patient patient){
        this.patient = patient;
    }

    public String getFirstName(){
        return patient.getName().getFirstName();
    }
    public String getLastName(){
        return patient.getName().getLastName();
    }
    public String getSsn(){
        return patient.getSSN();
    }
    public Date getDateOfBirth(){
        return patient.getDateOfBirth().toGregorianCalendar().getTime();
    }
}

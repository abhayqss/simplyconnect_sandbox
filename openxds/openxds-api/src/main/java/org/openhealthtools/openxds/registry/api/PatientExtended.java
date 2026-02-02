package org.openhealthtools.openxds.registry.api;

import com.misyshealthcare.connect.base.demographicdata.PhoneNumber;
import org.openhealthexchange.openpixpdq.data.Patient;

import java.util.List;

/**
 * Created by averazub on 11/28/2016.
 */
public class PatientExtended extends Patient {
    private String raceIdentifier;
    private String religionIdentifier;
    private String primaryLanguageIdentifier;
    private String maritalStatusIdentifier;
    private String ethnicGroupIdentifier;
    private String veteranStatusIdentifier;
    private String veteranStatus;
    private String nationalityIdentifier;
    private String nationality;
    private List<PhoneNumber> emails;

    public String getRaceIdentifier() {
        return raceIdentifier;
    }

    public void setRaceIdentifier(String raceIdentifier) {
        this.raceIdentifier = raceIdentifier;
    }

    public String getReligionIdentifier() {
        return religionIdentifier;
    }

    public void setReligionIdentifier(String religionIdentifier) {
        this.religionIdentifier = religionIdentifier;
    }

    public String getPrimaryLanguageIdentifier() {
        return primaryLanguageIdentifier;
    }

    public void setPrimaryLanguageIdentifier(String primaryLanguageIdentifier) {
        this.primaryLanguageIdentifier = primaryLanguageIdentifier;
    }

    public String getMaritalStatusIdentifier() {
        return maritalStatusIdentifier;
    }

    public void setMaritalStatusIdentifier(String maritalStatusIdentifier) {
        this.maritalStatusIdentifier = maritalStatusIdentifier;
    }

    public String getEthnicGroupIdentifier() {
        return ethnicGroupIdentifier;
    }

    public void setEthnicGroupIdentifier(String ethnicGroupIdentifier) {
        this.ethnicGroupIdentifier = ethnicGroupIdentifier;
    }

    public String getVeteranStatusIdentifier() {
        return veteranStatusIdentifier;
    }

    public void setVeteranStatusIdentifier(String veteranStatusIdentifier) {
        this.veteranStatusIdentifier = veteranStatusIdentifier;
    }

    public String getVeteranStatus() {
        return veteranStatus;
    }

    public void setVeteranStatus(String veteranStatus) {
        this.veteranStatus = veteranStatus;
    }

    public String getNationalityIdentifier() {
        return nationalityIdentifier;
    }

    public void setNationalityIdentifier(String nationalityIdentifier) {
        this.nationalityIdentifier = nationalityIdentifier;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public List<PhoneNumber> getEmails() {
        return emails;
    }

    public void setEmails(List<PhoneNumber> emails) {
        this.emails = emails;
    }

}

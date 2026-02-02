package com.scnsoft.eldermark.hl7v2.model;

import com.scnsoft.eldermark.entity.MPI;

import java.util.Calendar;

public class PersonIdentifier {
    private String id;
    private Identifier assigningAuthority;
    private String identifierTypeCode;
    private Identifier assigningFacility;
    private Calendar effectiveDate;
    private Calendar expirationDate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Identifier getAssigningAuthority() {
        return assigningAuthority;
    }

    public void setAssigningAuthority(Identifier assigningAuthority) {
        this.assigningAuthority = assigningAuthority;
    }

    public String getIdentifierTypeCode() {
        return identifierTypeCode;
    }

    public void setIdentifierTypeCode(String identifierTypeCode) {
        this.identifierTypeCode = identifierTypeCode;
    }

    public Identifier getAssigningFacility() {
        return assigningFacility;
    }

    public void setAssigningFacility(Identifier assigningFacility) {
        this.assigningFacility = assigningFacility;
    }

    public Calendar getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Calendar effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Calendar getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Calendar expirationDate) {
        this.expirationDate = expirationDate;
    }

    public static MPI createMPIFromPersonIdentifier(PersonIdentifier patientIdentifier) {
        return createOrUpdateMPIFromPersonIdentifier(null, patientIdentifier);
    }

    public static MPI createOrUpdateMPIFromPersonIdentifier(MPI personIdentifier, PersonIdentifier patientIdentifier) {
        if (personIdentifier == null) {
            personIdentifier = new MPI();
        }

        personIdentifier.setPatientId(patientIdentifier.getId());

        if (patientIdentifier.getAssigningAuthority() != null) {
            final Identifier assigningAuthorityIdentifier = patientIdentifier.getAssigningAuthority();

            String assignFacNam = assigningAuthorityIdentifier.getNamespaceId();
            String assignFacUniversal = removeAmpersandsEncoding(assigningAuthorityIdentifier.getUniversalId());
            String assignFacUniversalType = removeAmpersandsEncoding(assigningAuthorityIdentifier.getUniversalIdType());

            personIdentifier.setAssigningAuthorityNamespace(assignFacNam);
            personIdentifier.setAssigningAuthorityUniversal(assignFacUniversal);
            personIdentifier.setAssigningAuthorityUniversalType(assignFacUniversalType);

            String assignAuthority = null;
            if (assignFacNam != null && assignFacUniversal != null && assignFacUniversalType != null) {
                assignAuthority = assignFacNam + "&" + assignFacUniversal + "&" + assignFacUniversalType;
            } else if (assignFacNam == null && assignFacUniversal != null && assignFacUniversalType != null) {
                assignAuthority = "&" + assignFacUniversal + "&" + assignFacUniversalType;
            } else if (assignFacNam != null && assignFacUniversalType == null) {
                assignAuthority = assignFacNam + "&" + assignFacUniversal + "&";
            }

            personIdentifier.setAssigningAuthority(assignAuthority);
        }

        if (patientIdentifier.getEffectiveDate() != null) {
            personIdentifier.setEffectiveDate(patientIdentifier.getEffectiveDate().getTime());
        }

        if (patientIdentifier.getExpirationDate() != null) {
            personIdentifier.setExpirationDate(patientIdentifier.getExpirationDate().getTime());
        }

        personIdentifier.setIdentifierTypeCode(patientIdentifier.getIdentifierTypeCode());

        if (patientIdentifier.getAssigningFacility() != null) {
            final Identifier assigningFacilityIdentifier = patientIdentifier.getAssigningFacility();

            personIdentifier.setAssigningFacilityNamespace(assigningFacilityIdentifier.getNamespaceId());
            personIdentifier.setAssigningFacilityUniversal(removeAmpersandsEncoding(assigningFacilityIdentifier.getUniversalId()));
            personIdentifier.setAssigningFacilityUniversalType(removeAmpersandsEncoding(assigningFacilityIdentifier.getUniversalIdType()));
        }

        return personIdentifier;
    }

    public static String removeAmpersandsEncoding(String source) {
        if (source == null) {
            return null;
        }
        return source.replace("amp;", "");
    }

    @Override
    public String toString() {
        return "PersonIdentifier{" +
                "id='" + id + '\'' +
                ", assigningAuthority=" + assigningAuthority +
                ", identifierTypeCode='" + identifierTypeCode + '\'' +
                ", assigningFacility=" + assigningFacility +
                ", effectiveDate=" + effectiveDate +
                ", expirationDate=" + expirationDate +
                '}';
    }
}

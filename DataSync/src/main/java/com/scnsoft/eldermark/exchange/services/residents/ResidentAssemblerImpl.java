package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.MappingUtils;
import com.scnsoft.eldermark.exchange.fk.ResidentForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.Resident;
import com.scnsoft.eldermark.framework.DatabaseIdWithId;
import com.scnsoft.eldermark.framework.Utils;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ResidentAssemblerImpl implements ResidentAssembler {

    @Override
    public Resident.Updatable createResidentUpdatable(ResidentData source, ResidentForeignKeys foreignKeys) {
        return createResidentUpdatable(source, foreignKeys, null);
    }

    @Override
    public Resident.Updatable createMappedResidentUpdatable(ResidentData sourceResident, ResidentForeignKeys foreignKeys, DatabaseIdWithId targetOrganizationIdAndDatabaseId) {
        return createResidentUpdatable(sourceResident, foreignKeys, targetOrganizationIdAndDatabaseId.getId());
    }

    private  Resident.Updatable createResidentUpdatable(ResidentData source, ResidentForeignKeys foreignKeys, Long mappedOrganizationId) {
        Resident.Updatable updatable = new Resident.Updatable();
        updatable.setGenderId(foreignKeys.getGenderId());
        java.sql.Date admitDate = source.getAdmitDate();
        java.sql.Time admitTime = source.getAdmitTime();
        if (admitDate != null) {
            if (admitTime != null) {
                updatable.setAdmitDate(Utils.mergeDateTime(admitDate, admitTime));
            } else {
                updatable.setAdmitDate(admitDate);
            }
        }

        updatable.setBirthDate(source.getBirthDate());
        updatable.setAge(source.getAge());

        java.sql.Date dischargeDate = source.getDischargeDate();
        java.sql.Time dischargeTime = source.getDischargeTime();
        if (dischargeDate != null) {
            if (dischargeTime != null) {
                updatable.setDischargeDate(Utils.mergeDateTime(dischargeDate, dischargeTime));
            } else {
                updatable.setDischargeDate(dischargeDate);
            }
        }

        if (updatable.getDischargeDate() != null && updatable.getDischargeDate().compareTo(new Date()) <=0 ) {
            updatable.setActive(Boolean.FALSE);
        }

        updatable.setMaritalStatusId(foreignKeys.getMaritalStatusId());
        updatable.setRaceId(foreignKeys.getRaceId());
        updatable.setReligionId(foreignKeys.getReligionId());

        String ssn = source.getSocialSecurity();
        if (ssn!=null) ssn = ssn.replaceAll("-","");
        updatable.setSocialSecurity(ssn);
        if (ssn != null) {
            String ssnLastFourDigits;
            if (ssn.length() >= 4) {
                ssnLastFourDigits = ssn.substring(ssn.length() - 4);
            } else {
                ssnLastFourDigits = ssn;
            }
            updatable.setSsnLastFourDigits(ssnLastFourDigits);
        }
        updatable.setFacilityId(mappedOrganizationId != null ? mappedOrganizationId : foreignKeys.getFacilityOrganizationId());
        updatable.setProviderOrganizationId(mappedOrganizationId != null ? mappedOrganizationId : foreignKeys.getFacilityOrganizationId());
        updatable.setUnitNumber(source.getUnitNumber());

        updatable.setOptOut(source.getHealthExchangeOptOut());

        updatable.setAmbulancePreference(source.getAmbulancePreference());
        updatable.setEvacuationStatus(source.getEvacuationStatus());
        updatable.setHospitalOfPreference(source.getHospitalOfPreference());
        updatable.setTransportationPreference(source.getTransportationPreference());
        updatable.setVeteran(source.getVeteran());
        updatable.setMAAuthNumbExpireDate(source.getMAAuthNumbExpireDate());
        updatable.setMAAuthorizationNumber(source.getMAAuthorizationNumber());
        updatable.setMedicaidNumber(source.getMedicaidNumber());
        updatable.setMedicareNumber(source.getMedicareNumber());
        updatable.setMedicalRecordNumber(source.getMedicalRecordNumber());
        updatable.setPreAdmissionNumber(source.getPreAdmissionNumber());

        updatable.setPrevAddrCity(source.getPrevAddrCity());   // move to PersonAddress with qualifier?
        updatable.setPrevAddrState(source.getPrevAddrState());
        updatable.setPrevAddrStreet(source.getPrevAddrStreet());
        updatable.setPrevAddrZip(source.getPrevAddrZip());

        updatable.setDentalInsurance(source.getDentalInsurance());

        updatable.setAdvanceDirectiveFreeText(source.getAdvanceDirectives());

        updatable.setFirstName(source.getFirstName());
        updatable.setLastName(source.getLastName());
        updatable.setMiddleName(source.getMiddleName());
        updatable.setPreferredName(source.getPreferredName());
        updatable.setPharmacyPid(source.getPharmacyPid());

        return updatable;
    }

    @Override
    public Resident createResident(ResidentData sourceResident, long personId, long custodianId, long databaseId, ResidentForeignKeys foreignKeys, String hieConsentPolicy) {
        Resident resident = new Resident();
        resident.setDatabaseId(databaseId);
        resident.setLegacyId(String.valueOf(sourceResident.getId()));
        resident.setPersonId(personId);
        resident.setCustodianId(custodianId);
        resident.setHieConsentPolicyType(hieConsentPolicy);
        resident.setUpdatable(createResidentUpdatable(sourceResident, foreignKeys));
        return resident;
    }

    @Override
    public Resident createMappedResident(ResidentData sourceResident, long personId, long custodianId, ResidentForeignKeys foreignKeys,
                                         DatabaseIdWithId targetOrganizationIdAndDatabaseId, String mappedResidentHieConsentPolicy) {
        Resident resident = new Resident();
        resident.setDatabaseId(targetOrganizationIdAndDatabaseId.getDatabaseId());
        resident.setLegacyId(MappingUtils.generateMappedLegacyId(sourceResident.getId(), foreignKeys.getFacilityOrganizationId()));
        resident.setPersonId(personId);
        resident.setCustodianId(custodianId);
        resident.setHieConsentPolicyType(mappedResidentHieConsentPolicy);
        resident.setUpdatable(createMappedResidentUpdatable(sourceResident, foreignKeys, targetOrganizationIdAndDatabaseId));
        return resident;
    }
}

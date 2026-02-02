package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.server.dao.CcdCodeDao;
import com.scnsoft.eldermark.consana.sync.server.model.entity.CcdCode;
import com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils;
import org.hl7.fhir.instance.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static com.scnsoft.eldermark.consana.sync.server.constants.FhirConstants.*;
import static com.scnsoft.eldermark.consana.sync.server.model.enums.IdentifierCode.MRN;
import static com.scnsoft.eldermark.consana.sync.server.model.enums.IdentifierCode.SSN;
import static com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils.fetchIdentifier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@MockitoSettings
class FhirConversionUtilsTest {

    @Mock
    CcdCodeDao ccdCodeDao;

    @InjectMocks
    FhirConversionUtils fhirConversionUtils;

    @Test
    void convertGender_female_toF() {
        var code = new CcdCode();

        when(ccdCodeDao.getFirstByCodeAndCodeSystem("F", ADMINISTRATIVE_GENDER_CODE_SYSTEM)).thenReturn(code);

        var result = fhirConversionUtils.convertGender(Enumerations.AdministrativeGender.FEMALE);

        assertEquals(code, result);
    }

    @Test
    void convertGender_male_toM() {
        var code = new CcdCode();

        when(ccdCodeDao.getFirstByCodeAndCodeSystem("M", ADMINISTRATIVE_GENDER_CODE_SYSTEM)).thenReturn(code);

        var result = fhirConversionUtils.convertGender(Enumerations.AdministrativeGender.MALE);

        assertEquals(code, result);
    }

    @Test
    void convertGender_OtherUnknownNull_toNull() {

        var result1 = fhirConversionUtils.convertGender(Enumerations.AdministrativeGender.OTHER);
        var result2 = fhirConversionUtils.convertGender(Enumerations.AdministrativeGender.NULL);
        var result3 = fhirConversionUtils.convertGender(Enumerations.AdministrativeGender.UNKNOWN);
        var result4 = fhirConversionUtils.convertGender(null);

        assertNull(result1);
        assertNull(result2);
        assertNull(result3);
        assertNull(result4);

        verifyNoInteractions(ccdCodeDao);
    }

    @Test
    void convertMaritalStatus_hasCodeFromMaritalStatus_ShouldFetchFromDatabase() {
        var code = "code";
        var maritalStatusConcept = new CodeableConcept();
        var coding = new Coding();
        coding.setCode(code);
        coding.setSystem(MARITAL_STATUS_CODE_SYSTEM);
        maritalStatusConcept.addCoding(coding);
        var ccdCode = new CcdCode();

        when(ccdCodeDao.getFirstByCodeAndCodeSystem(code, MARITAL_STATUS_CODE_SYSTEM)).thenReturn(ccdCode);

        var result = fhirConversionUtils.convertMaritalStatus(maritalStatusConcept);

        assertEquals(ccdCode, result);
    }

    @Test
    void convertMaritalStatus_dontHaveCodeFromMaritalStatus_ShouldReturnNull() {
        var maritalStatusConcept = new CodeableConcept();

        var result = fhirConversionUtils.convertMaritalStatus(maritalStatusConcept);

        assertNull(result);
        verifyNoInteractions(ccdCodeDao);
    }

    @Test
    void fetchSsn_IfSSIdentifierPresent_ShouldReturnIn() {
        var identifier = new Identifier();
        var coding = new Coding();
        coding.setSystem(V2_IDENTIFIER_TYPE);
        coding.setCode(SSN.getCode());
        var concept = new CodeableConcept();
        concept.addCoding(coding);
        identifier.setType(concept);
        var ssn = "123456789";
        identifier.setValue(ssn);
        var patient = new Patient();
        patient.addIdentifier(identifier);

        var result = fetchIdentifier(patient, SSN);

        assertEquals(ssn, result);
    }


    @Test
    void fetchSsn_IfSSIdentifierNotPresent_ShouldReturnNull() {
        var coding = new Coding();
        coding.setSystem(V2_IDENTIFIER_TYPE);
        coding.setCode(MRN.getCode());
        var concept = new CodeableConcept();
        concept.addCoding(coding);

        var identifier = new Identifier();
        identifier.setType(concept);

        var mr = "123456789";
        identifier.setValue(mr);

        var patient = new Patient();
        patient.addIdentifier(identifier);


        var result = fetchIdentifier(patient, SSN);


        assertNull(result);
    }

    @Test
    void convertRace_ifPresent_ReturnsIt() {
        var raceText = "race";

        var concept = new CodeableConcept();
        concept.setText(raceText);

        var extension = new Extension();
        extension.setUrl(RACE_EXTENSION_URL);
        extension.setValue(concept);

        var patient = new Patient();
        patient.addExtension(extension);

        var ccdCode = new CcdCode();


        when(ccdCodeDao.getFirstByValueSetAndDisplayName(RACE_VALUE_SET, raceText)).thenReturn(ccdCode);


        var result = fhirConversionUtils.convertRace(patient);


        assertEquals(ccdCode, result);
    }

    @Test
    void convertRace_ifNotPresent_ReturnsNull() {
        var patient = new Patient();

        var result = fhirConversionUtils.convertRace(patient);

        assertNull(result);
        verifyNoInteractions(ccdCodeDao);
    }
}
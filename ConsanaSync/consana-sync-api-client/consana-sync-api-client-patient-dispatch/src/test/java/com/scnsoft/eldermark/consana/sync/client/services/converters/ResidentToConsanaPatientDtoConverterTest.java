package com.scnsoft.eldermark.consana.sync.client.services.converters;

import com.scnsoft.eldermark.consana.sync.client.model.entities.Database;
import com.scnsoft.eldermark.consana.sync.client.model.entities.Resident;
import org.hl7.fhir.instance.model.Patient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class ResidentToConsanaPatientDtoConverterTest {

    @InjectMocks
    private ResidentToConsanaPatientDtoConverter instance;

    @Test
    void convert_ConvertedWithCorrectIdentifier_ShouldReturnCorrectDto() {
        var resident = prepareResident();
        var patient = new Patient();
        patient.setId("AbCdE");

        var dto = instance.convert(Pair.of(resident, patient));

        assertNotNull(dto);
        var identifiers = dto.getIdentifier();
        assertEquals(1, identifiers.size());
        assertEquals("http://xchangelabs.com/fhir/patient-id", identifiers.get(0).getSystem());
        assertEquals("AbCdE", identifiers.get(0).getValue());
    }

    private Resident prepareResident() {
        var resident = new Resident(2L, "xref");
        var database = new Database(3L, "orgOid");
        resident.setDatabase(database);
        return resident;
    }
}

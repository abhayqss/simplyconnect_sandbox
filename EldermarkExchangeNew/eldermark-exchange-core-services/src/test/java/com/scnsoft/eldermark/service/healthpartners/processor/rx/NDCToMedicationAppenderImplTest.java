package com.scnsoft.eldermark.service.healthpartners.processor.rx;

import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;
import com.scnsoft.eldermark.service.document.cda.CcdCodeCustomService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NDCToMedicationAppenderImplTest {

    @Mock
    private CcdCodeCustomService ccdCodeCustomService;

    @InjectMocks
    private NDCToMedicationAppenderImpl instance;


    @Test
    void process_NDCDidntExist_NewAdded() {
        var ndc = "12345";
        var drugName = "drugName";

        var medication = new Medication();
        var medInfo = new MedicationInformation();
        medication.setMedicationInformation(medInfo);

        var addedCcdCode = new CcdCode();

        when(ccdCodeCustomService.findOrCreate(ndc, drugName, CodeSystem.NDC)).thenReturn(Optional.of(addedCcdCode));

        instance.addUniqueNdcToMedication(medication, ndc, drugName);

        assertThat(medInfo.getTranslationProductCodes()).containsExactly(addedCcdCode);
    }

    @Test
    void process_SameNDCExisted_NewNotAdded() {
        var ndc = "12345";
        var drugName = "drugName";

        var medication = new Medication();
        var medInfo = new MedicationInformation();
        medication.setMedicationInformation(medInfo);

        var existingCcdCode = new CcdCode();
        existingCcdCode.setCodeSystem(CodeSystem.NDC.getOid());
        existingCcdCode.setCode(ndc);
        medInfo.setTranslationProductCodes(List.of(existingCcdCode));

        instance.addUniqueNdcToMedication(medication, ndc, drugName);

        assertThat(medInfo.getTranslationProductCodes())
                .containsExactly(existingCcdCode);

        verifyNoInteractions(ccdCodeCustomService);
    }

    @Test
    void process_differentNDCExisted_NewAdded() {
        var ndc = "12345";
        var drugName = "drugName";
        var medication = new Medication();
        var medInfo = new MedicationInformation();
        medication.setMedicationInformation(medInfo);

        var existingCcdCode = new CcdCode();
        existingCcdCode.setCodeSystem(CodeSystem.NDC.getOid());
        existingCcdCode.setCode(ndc + "1234444");
        medInfo.setTranslationProductCodes(Lists.newArrayList(existingCcdCode));

        var addedCcdCode = new CcdCode();

        when(ccdCodeCustomService.findOrCreate(ndc, drugName, CodeSystem.NDC)).thenReturn(Optional.of(addedCcdCode));

        instance.addUniqueNdcToMedication(medication, ndc, drugName);

        assertThat(medInfo.getTranslationProductCodes())
                .containsExactlyInAnyOrder(existingCcdCode, addedCcdCode);
    }
}
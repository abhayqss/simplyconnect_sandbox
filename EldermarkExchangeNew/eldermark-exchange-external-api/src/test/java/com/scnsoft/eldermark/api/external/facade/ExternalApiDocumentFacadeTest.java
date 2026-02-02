package com.scnsoft.eldermark.api.external.facade;

import com.scnsoft.eldermark.api.external.service.ResidentsService;
import com.scnsoft.eldermark.api.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.entity.document.DocumentReport;
import com.scnsoft.eldermark.service.document.cda.generator.CcdGeneratorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalApiDocumentFacadeTest {
    @Mock
    private CcdGeneratorService ccdGeneratorService;
    @Mock
    private ResidentsService residentsService;

    @InjectMocks
    private ExternalApiDocumentFacadeImpl externalApiDocumentFacadeImpl;

    @Test
    public void testGenerateContinuityOfCareDocument_Aggregated() {
        testGenerateContinuityOfCareDocument(true);
    }

    @Test
    public void testGenerateContinuityOfCareDocument_NotAggregated() {
        testGenerateContinuityOfCareDocument(false);
    }


    private void testGenerateContinuityOfCareDocument(boolean isAggregated) {
        var report = new DocumentReport();
        final Long residentId = TestDataGenerator.randomId();

        when(ccdGeneratorService.generate(residentId, isAggregated)).thenReturn(report);

        var result = externalApiDocumentFacadeImpl.generateContinuityOfCareDocument(residentId, isAggregated);

        assertSame(report, result);
        verify(residentsService).checkAccessOrThrow(residentId);
    }
}

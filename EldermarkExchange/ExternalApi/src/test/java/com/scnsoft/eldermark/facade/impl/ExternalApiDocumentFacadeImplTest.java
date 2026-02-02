package com.scnsoft.eldermark.facade.impl;

import com.scnsoft.eldermark.facades.DocumentFacade;
import com.scnsoft.eldermark.service.ResidentsService;
import com.scnsoft.eldermark.services.Report;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ExternalApiDocumentFacadeImplTest {

    @Mock
    private DocumentFacade documentFacade;

    @Mock
    private ResidentsService residentsService;

    @InjectMocks
    private ExternalApiDocumentFacadeImpl externalApiDocumentFacadeImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGenerateContinuityOfCareDocument_Aggregated() {
        testGenerateContinuityOfCareDocument(true);
    }

    @Test
    public void testGenerateContinuityOfCareDocument_NotAggregated() {
        testGenerateContinuityOfCareDocument(false);
    }


    private void testGenerateContinuityOfCareDocument(boolean isAggregated) {
        final Report report = new Report();
        final Long residentId = TestDataGenerator.randomId();

        when(documentFacade.generateReport(residentId, isAggregated, "ccd")).thenReturn(report);

        final Report result = externalApiDocumentFacadeImpl.generateContinuityOfCareDocument(residentId, isAggregated);

        assertEquals(report, result);
        verify(residentsService).checkAccessOrThrow(residentId);
    }
}

package com.scnsoft.eldermark.service.impl;

import com.google.common.base.Optional;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.service.BaseServiceTest;
import com.scnsoft.eldermark.service.PrivilegesService;
import com.scnsoft.eldermark.services.converters.ConsanaXrefPatientDtoToResidentConverter;
import com.scnsoft.eldermark.shared.ConsanaXrefPatientIdDto;
import com.scnsoft.eldermark.shared.exception.PhrException;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ConsanaServiceImplTest extends BaseServiceTest {

    @Mock
    private PrivilegesService privilegesService;

    @Mock
    private ConsanaXrefPatientDtoToResidentConverter consanaXrefPatientDtoToResidentConverter;

    @InjectMocks
    private ConsanaServiceImpl consanaService;

    private static final String IDENTIFIER = "identifier";
    private static final String ORGANIZATION_OID = "organizationOid";
    private static final String COMMUNITY_OID = "communityoid";


    @Test(expected = PhrException.class)
    public void testGetResidentIdByXref_NoAccess_Throws() {
        when(privilegesService.hasConsanaAccess()).thenReturn(false);

        consanaService.getResidentIdByXref(new ConsanaXrefPatientIdDto(IDENTIFIER, ORGANIZATION_OID, COMMUNITY_OID));
    }

    @Test
    public void testGetResidentIdByXref_HasAccess_ReturnsOptional() throws Exception {
        final ConsanaXrefPatientIdDto consanaXrefPatientIdDto = new ConsanaXrefPatientIdDto(IDENTIFIER, ORGANIZATION_OID, COMMUNITY_OID);
        final Long residentId = 1L;
        final Resident resident = new Resident(residentId);
        final Optional<Resident> expected = Optional.of(resident);

        when(privilegesService.hasConsanaAccess()).thenReturn(true);
        when(consanaXrefPatientDtoToResidentConverter.convert(consanaXrefPatientIdDto)).thenReturn(expected);

        assertEquals(Optional.of(residentId), consanaService.getResidentIdByXref(consanaXrefPatientIdDto));
    }
}

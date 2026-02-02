package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.ConsanaXrefPatientIdDto;
import com.scnsoft.eldermark.api.shared.exception.PhrException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsanaServiceTest {

    @Mock
    private PrivilegesService privilegesService;

    @Mock
    private ConsanaClientXrefIdResolver consanaClientXrefIdResolver;

    @InjectMocks
    private ConsanaServiceImpl consanaService;

    private static final String IDENTIFIER = "identifier";
    private static final String ORGANIZATION_OID = "organizationOid";
    private static final String COMMUNITY_OID = "communityoid";

    @Test
    public void testGetResidentIdByXref_NoAccess_Throws() {
        var input = new ConsanaXrefPatientIdDto(IDENTIFIER, ORGANIZATION_OID, COMMUNITY_OID);

        when(privilegesService.hasConsanaAccess()).thenReturn(false);

        assertThrows(PhrException.class, () ->
                consanaService.getResidentIdByXref(input));
    }

    @Test
    public void testGetResidentIdByXref_HasAccess_ReturnsOptional() {
        var input = new ConsanaXrefPatientIdDto(IDENTIFIER, ORGANIZATION_OID, COMMUNITY_OID);
        final Long residentId = 1L;
        var expected = Optional.of(residentId);

        when(privilegesService.hasConsanaAccess()).thenReturn(true);
        when(consanaClientXrefIdResolver.resolveClientId(input)).thenReturn(expected);

        var actual = consanaService.getResidentIdByXref(input);

        assertEquals(Optional.of(residentId), actual);
    }

}
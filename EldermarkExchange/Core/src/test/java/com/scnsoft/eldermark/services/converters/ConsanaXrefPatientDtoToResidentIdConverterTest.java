package com.scnsoft.eldermark.services.converters;

import com.google.common.base.Optional;
import com.scnsoft.eldermark.dao.ResidentJpaDao;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.predicates.ConsanaCommunityIntegrationEnabledPredicate;
import com.scnsoft.eldermark.shared.ConsanaXrefPatientIdDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class ConsanaXrefPatientDtoToResidentIdConverterTest {

    private static final String IDENTIFIER = "identifier";
    private static final String ORGANIZATION_OID = "organizationOid";
    private static final String COMMUNITY_OID = "communityOid";

    @Mock
    private ResidentJpaDao residentJpaDao;

    @Mock
    private ConsanaCommunityIntegrationEnabledPredicate consanaCommunityIntegrationEnabledPredicate;

    @InjectMocks
    private ConsanaXrefPatientDtoToResidentConverter consanaXrefPatientDtoToResidentConverter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConvert_NullDto_ReturnsEmpty() throws Exception {
        final ConsanaXrefPatientIdDto consanaXrefPatientIdDto = null;
        testConvert_WithMissingData_ReturnsEmpty(consanaXrefPatientIdDto);
    }

    @Test
    public void testConvert_NoId_ReturnsEmpty() throws Exception {
        final ConsanaXrefPatientIdDto consanaXrefPatientIdDto = new ConsanaXrefPatientIdDto(StringUtils.EMPTY, ORGANIZATION_OID, COMMUNITY_OID);
        testConvert_WithMissingData_ReturnsEmpty(consanaXrefPatientIdDto);
    }

    @Test
    public void testConvert_NoOrganizationOid_ReturnsEmpty() throws Exception {
        final ConsanaXrefPatientIdDto consanaXrefPatientIdDto = new ConsanaXrefPatientIdDto(IDENTIFIER, StringUtils.EMPTY, COMMUNITY_OID);
        testConvert_WithMissingData_ReturnsEmpty(consanaXrefPatientIdDto);
    }


    @Test
    public void testConvert_OrganizationOid_ReturnsEmpty() throws Exception {
        final ConsanaXrefPatientIdDto consanaXrefPatientIdDto = new ConsanaXrefPatientIdDto(IDENTIFIER, StringUtils.EMPTY, COMMUNITY_OID);
        testConvert_WithMissingData_ReturnsEmpty(consanaXrefPatientIdDto);
    }

    private void testConvert_WithMissingData_ReturnsEmpty(ConsanaXrefPatientIdDto consanaXrefPatientIdDto) {
        assertFalse(consanaXrefPatientDtoToResidentConverter.convert(consanaXrefPatientIdDto).isPresent());
    }

    @Test
    public void testConvert_NotFound_ReturnsEmpty() throws Exception {
        final ConsanaXrefPatientIdDto consanaXrefPatientIdDto = new ConsanaXrefPatientIdDto(IDENTIFIER, ORGANIZATION_OID, COMMUNITY_OID);
        when(residentJpaDao.findFirstByConsanaXrefIdAndDatabaseOidAndFacilityOid(IDENTIFIER, ORGANIZATION_OID, COMMUNITY_OID))
                .thenReturn(Optional.<Resident>absent());

        assertFalse(consanaXrefPatientDtoToResidentConverter.convert(consanaXrefPatientIdDto).isPresent());
    }

    @Test
    public void testConvert_FoundAndIntegrationNotEnabled_ReturnsEmpty() throws Exception {
        final Resident resident = new Resident();
        resident.setFacility(new Organization());
        final ConsanaXrefPatientIdDto consanaXrefPatientIdDto = new ConsanaXrefPatientIdDto(IDENTIFIER, ORGANIZATION_OID, COMMUNITY_OID);

        when(residentJpaDao.findFirstByConsanaXrefIdAndDatabaseOidAndFacilityOid(IDENTIFIER, ORGANIZATION_OID, COMMUNITY_OID))
                .thenReturn(Optional.of(resident));
        when(consanaCommunityIntegrationEnabledPredicate.apply(any(Organization.class))).thenReturn(false);

        final Optional<Resident> result = consanaXrefPatientDtoToResidentConverter.convert(consanaXrefPatientIdDto);
        assertFalse(result.isPresent());
    }

    @Test
    public void testConvert_FoundAndIntegrationEnabled_ReturnsResidentId() throws Exception {
        final Resident resident = new Resident();
        resident.setFacility(new Organization());
        final ConsanaXrefPatientIdDto consanaXrefPatientIdDto = new ConsanaXrefPatientIdDto(IDENTIFIER, ORGANIZATION_OID, COMMUNITY_OID);

        when(residentJpaDao.findFirstByConsanaXrefIdAndDatabaseOidAndFacilityOid(IDENTIFIER, ORGANIZATION_OID, COMMUNITY_OID))
                .thenReturn(Optional.of(resident));
        when(consanaCommunityIntegrationEnabledPredicate.apply(any(Organization.class))).thenReturn(true);

        final Optional<Resident> result = consanaXrefPatientDtoToResidentConverter.convert(consanaXrefPatientIdDto);

        assertTrue(result.isPresent());
        assertEquals(resident, result.get());
    }
}
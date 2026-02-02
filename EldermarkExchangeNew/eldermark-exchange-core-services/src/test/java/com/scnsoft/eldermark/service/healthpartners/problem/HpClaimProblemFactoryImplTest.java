package com.scnsoft.eldermark.service.healthpartners.problem;

import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.beans.projection.IdOrganizationIdActiveAware;
import com.scnsoft.eldermark.dao.ProblemDao;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.Author;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.ccd.Problem;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersMedClaim;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.CcdCodeService;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.service.document.cda.CcdCodeCustomService;
import com.scnsoft.eldermark.service.healthpartners.HealthPartnersUtils;
import com.scnsoft.eldermark.service.healthpartners.author.HpMedicalProfessionalAuthorFactory;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HpClaimProblemFactoryImplTest {

    private final Long problemTypeCodeId = 1111L;
    private final CcdCode problemTypeCode = new CcdCode();

    {
        problemTypeCode.setId(problemTypeCodeId);
    }

    private CcdCode problemValueCode;

    @Mock
    private ClientService clientService;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private ProblemDao problemDao;

    @Mock
    private CcdCodeService ccdCodeService;

    @Mock
    private CcdCodeCustomService ccdCodeCustomService;

    @Mock
    private HpMedicalProfessionalAuthorFactory hpMedicalProfessionalAuthorFactory;

    private HpClaimProblemFactoryImpl instance;

    @BeforeEach
    void init() {
        when(ccdCodeService.findByCodeAndValueSet(HpClaimProblemFactoryImpl.DIAGNOSIS_TYPE_CODE, ValueSetEnum.PROBLEM_TYPE_2006)).thenReturn(problemTypeCode);
        instance = new HpClaimProblemFactoryImpl(clientService, organizationService, problemDao, ccdCodeCustomService, hpMedicalProfessionalAuthorFactory, ccdCodeService);
    }

    @Test
    void createProblem_IcdVersionNull_defaultsToIcd10() {
        createProblem(null, CodeSystem.ICD_10_CM, true);
    }

    @Test
    void createProblem_IcdVersion10_CodeSystemIcd10() {
        createProblem(10, CodeSystem.ICD_10_CM, true);
    }

    @Test
    void createProblem_IcdVersion9_CodeSystemIcd9() {
        createProblem(9, CodeSystem.ICD_9_CM, true);
    }

    @Test
    void createProblem_unsupportedIcdVersion_Throws() {
        var client = new Client();
        var org = new Organization();
        org.setId(3L);
        client.setOrganizationId(org.getId());
        client.setOrganization(org);

        var clientProjection = createClientProjection(client.getId(), org.getId(), null);
        var claim = createClaim(-5, true);

        when(organizationService.getOne(clientProjection.getOrganizationId())).thenReturn(org);
        when(clientService.getById(clientProjection.getId())).thenReturn(client);
        when(ccdCodeService.getOne(problemTypeCodeId)).thenReturn(problemTypeCode);

        assertThrows(ValidationException.class, () -> instance.createProblem(claim, clientProjection));
    }

    @Test
    void createProblem_noPhysicianFields_AuthorIsNull() {
        createProblem(10, CodeSystem.ICD_10_CM, false);
    }

    private void createProblem(Integer icdVersion, CodeSystem expectedCodeSystem, boolean addPhysician) {
        var client = new Client(1L);
        var org = new Organization();
        org.setId(3L);
        client.setOrganizationId(org.getId());
        client.setOrganization(org);

        var clientProjection = createClientProjection(client.getId(), org.getId(), null);
        var claim = createClaim(icdVersion, addPhysician);
        var author = new Author();
        problemValueCode = new CcdCode();
        problemValueCode.setCode("code");
        problemValueCode.setCodeSystem("codeSystem");
        problemValueCode.setCodeSystemName("codeSystemName");
        problemValueCode.setDisplayName("codeSystemDisplay");

        when(organizationService.getOne(clientProjection.getOrganizationId())).thenReturn(org);
        when(clientService.getById(clientProjection.getId())).thenReturn(client);
        when(ccdCodeService.getOne(problemTypeCodeId)).thenReturn(problemTypeCode);
        when(ccdCodeCustomService.findOrCreate(claim.getDiagnosisCode(), claim.getDiagnosisTxt(), expectedCodeSystem)).thenReturn(Optional.of(problemValueCode));
        doAnswer(returnsFirstArg()).when(problemDao).save(any(Problem.class));
        if (addPhysician) {
            when(hpMedicalProfessionalAuthorFactory.createAuthor(org, HealthPartnersUtils.PROBLEM_CLAIM_LEGACY_TABLE, claim.getPhysicianFirstName(), claim.getPhysicianMiddleName(), claim.getPhysicianLastName())).thenReturn(author);
        }

        var result = instance.createProblem(claim, clientProjection);

        verifyProblem(client, claim, author, result);
    }

    private IdOrganizationIdActiveAware createClientProjection(Long id, Long orgId, Boolean active) {
        return new IdOrganizationIdActiveAware() {
            @Override
            public Boolean getActive() {
                return active;
            }

            @Override
            public Long getId() {
                return id;
            }

            @Override
            public Long getOrganizationId() {
                return orgId;
            }
        };
    }

    private void verifyProblem(Client client, HealthPartnersMedClaim claim, Author author, Problem result) {
        assertThat(result).isNotNull();

        assertThat(result.getOrganization()).isEqualTo(client.getOrganization());
        assertThat(result.getOrganizationId()).isEqualTo(client.getOrganization().getId());

        assertThat(result.getClient()).isEqualTo(client);
        assertThat(result.getClientId()).isEqualTo(client.getId());

        assertThat(result.getStatusCode()).isEqualTo("active");
        assertThat(result.getTimeLow()).isEqualTo(DateTimeUtils.toDate(claim.getServiceDate()));

        assertThat(result.getTimeHigh()).isNull();
        assertThat(result.getLegacyId()).isEqualTo(0);
        assertThat(result.getRank()).isNull();

        verifyProblemObservations(client.getOrganization(), claim, author, result);
    }

    private HealthPartnersMedClaim createClaim(Integer icdVersion, boolean addPhysician) {
        var claim = new HealthPartnersMedClaim();

        claim.setServiceDate(Instant.now());
        claim.setDiagnosisCode("J43.9");
        claim.setDiagnosisTxt("Emphysema, unspecified");
        claim.setIcdVersion(icdVersion);

        if (addPhysician) {
            claim.setPhysicianFirstName("first");
            claim.setPhysicianMiddleName("middle");
            claim.setPhysicianLastName("last");
        }

        return claim;
    }

    private void verifyProblemObservations(Organization organization, HealthPartnersMedClaim claim, Author author, Problem result) {
        assertThat(result.getProblemObservations()).hasSize(1);

        var observation = result.getProblemObservations().get(0);

        assertThat(observation.getProblem()).isEqualTo(result);

        assertThat(observation.getOrganization()).isEqualTo(organization);
        assertThat(observation.getOrganizationId()).isEqualTo(organization.getId());

        assertThat(observation.getProblemDateTimeLow()).isEqualTo(DateTimeUtils.toDate(claim.getServiceDate()));

        assertThat(observation.getProblemType()).isEqualTo(problemTypeCode);
        assertThat(observation.getProblemCode()).isEqualTo(problemValueCode);
        assertThat(observation.getProblemIcdCode()).isEqualTo(problemValueCode.getCode());
        assertThat(observation.getProblemIcdCodeSet()).isEqualTo(problemValueCode.getCodeSystemName());

        verifyAuthor(observation.getAuthor(), author, claim);

        assertThat(observation.getManual()).isFalse();

        assertThat(observation.getLegacyId()).isEqualTo(0);
        assertThat(observation.getProblemDateTimeHigh()).isNull();
        assertThat(observation.getAgeObservationUnit()).isNull();
        assertThat(observation.getAgeObservationValue()).isNull();
        assertThat(observation.getHealthStatusObservationText()).isNull();
        assertThat(observation.getProblemName()).isNull();
        assertThat(observation.getProblemStatusText()).isNull();
        assertThat(observation.getNegationInd()).isNull();
        assertThat(observation.getHealthStatusCode()).isNull();
        assertThat(observation.getPrimary()).isNull();
        assertThat(observation.getRecordedDate()).isNull();
        assertThat(observation.getComments()).isNull();
        assertThat(observation.getConsanaId()).isNull();

    }

    private void verifyAuthor(Author author, Author expectedAuthor, HealthPartnersMedClaim csvData) {
        if (StringUtils.isAllEmpty(csvData.getPhysicianFirstName(), csvData.getPhysicianMiddleName(), csvData.getPhysicianLastName())) {
            assertThat(author).isNull();
        } else {
            assertThat(author).isEqualTo(expectedAuthor);
        }
    }
}
package com.scnsoft.eldermark.service.healthpartners.problem;

import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.beans.projection.IdOrganizationIdActiveAware;
import com.scnsoft.eldermark.dao.ProblemDao;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.ccd.Problem;
import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Component
public class HpClaimProblemFactoryImpl implements HpClaimProblemFactory {

    final static String DIAGNOSIS_TYPE_CODE = "282291009";

    private final ClientService clientService;
    private final OrganizationService organizationService;
    private final ProblemDao problemDao;
    private final CcdCodeCustomService ccdCodeCustomService;
    private final HpMedicalProfessionalAuthorFactory hpMedicalProfessionalAuthorFactory;

    private final CcdCodeService ccdCodeService;
    private final Long diagnosisTypeCodeId;

    @Autowired
    public HpClaimProblemFactoryImpl(ClientService clientService,
                                     OrganizationService organizationService,
                                     ProblemDao problemDao,
                                     CcdCodeCustomService ccdCodeCustomService,
                                     HpMedicalProfessionalAuthorFactory hpMedicalProfessionalAuthorFactory,
                                     CcdCodeService ccdCodeService) {
        this.clientService = clientService;
        this.organizationService = organizationService;
        this.problemDao = problemDao;
        this.ccdCodeCustomService = ccdCodeCustomService;
        this.hpMedicalProfessionalAuthorFactory = hpMedicalProfessionalAuthorFactory;
        this.ccdCodeService = ccdCodeService;
        this.diagnosisTypeCodeId = ccdCodeService.findByCodeAndValueSet(DIAGNOSIS_TYPE_CODE, ValueSetEnum.PROBLEM_TYPE_2006).getId();
    }

    @Override
    @Transactional
    public Problem createProblem(HealthPartnersMedClaim claim, IdOrganizationIdActiveAware client) {
        var problem = new Problem();

        var organization = organizationService.getOne(client.getOrganizationId());

        problem.setOrganization(organization);
        problem.setOrganizationId(client.getOrganizationId());

        problem.setClient(clientService.getById(client.getId()));
        problem.setClientId(client.getId());

        problem.setStatusCode("active");
        problem.setTimeLow(DateTimeUtils.toDate(claim.getServiceDate()));

        problem.setProblemObservations(new ArrayList<>());

        var observation = createProblemObservation(claim, organization);
        problem.getProblemObservations().add(observation);
        observation.setProblem(problem);

        return problemDao.save(problem);
    }

    private ProblemObservation createProblemObservation(HealthPartnersMedClaim claim, Organization organization) {
        var observation = new ProblemObservation();

        observation.setOrganization(organization);
        observation.setOrganizationId(organization.getId());

        observation.setProblemDateTimeLow(DateTimeUtils.toDate(claim.getServiceDate()));

        observation.setProblemType(ccdCodeService.getOne(diagnosisTypeCodeId));

        var valueCode = resolveValueCode(claim);
        observation.setProblemCode(valueCode);
        observation.setProblemIcdCode(valueCode.getCode());
        observation.setProblemIcdCodeSet(valueCode.getCodeSystemName());

        addAuthor(observation, organization, claim);

        observation.setManual(false);

        return observation;
    }

    private void addAuthor(ProblemObservation observation, Organization organization, HealthPartnersMedClaim claim) {
        if (!StringUtils.isAllEmpty(
                claim.getPhysicianFirstName(),
                claim.getPhysicianMiddleName(),
                claim.getPhysicianLastName())
        ) {
            var author = hpMedicalProfessionalAuthorFactory.createAuthor(
                    organization,
                    HealthPartnersUtils.PROBLEM_CLAIM_LEGACY_TABLE,
                    claim.getPhysicianFirstName(),
                    claim.getPhysicianMiddleName(),
                    claim.getPhysicianLastName()
            );

            observation.setAuthor(author);
        }
    }

    private CcdCode resolveValueCode(HealthPartnersMedClaim claim) {
        var codeSystem = resolveIcdCodeSystem(claim.getIcdVersion());
        return ccdCodeCustomService.findOrCreate(claim.getDiagnosisCode(), claim.getDiagnosisTxt(), codeSystem)
                .orElseThrow(
                        () -> new ValidationException("Failed to get or create diagnosis code "
                                + claim.getDiagnosisCode())
                );
    }

    private CodeSystem resolveIcdCodeSystem(Integer icdVersion) {
        if (icdVersion == null || icdVersion.equals(10)) {
            return CodeSystem.ICD_10_CM;
        }
        if (icdVersion.equals(9)) {
            return CodeSystem.ICD_9_CM;
        }
        throw new ValidationException("Unknown Icd version :" + icdVersion);
    }

}

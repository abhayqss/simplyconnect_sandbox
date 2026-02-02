package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.dao.target.CcdCodeDao;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.*;
import com.scnsoft.eldermark.framework.DatabaseInfo;
import com.scnsoft.eldermark.framework.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class PayerAssemblerImpl implements PayerAssembler {
    @Autowired
    private CcdCodeDao ccdCodeDao;

    private CcdCode selfRoleCode;

    @PostConstruct
    public void init() {
        selfRoleCode = ccdCodeDao.getCode("SELF", "2.16.840.1.113883.5.111");
    }

    public List<ResidentHealthPlan> getResidentHealthPlans(ResidentData sourceResident, long residentNewId, long databaseId) {
        if (sourceResident.getHealthPlan() == null)
            return Collections.emptyList();

        List<ResidentHealthPlan> healthPlans = new ArrayList<ResidentHealthPlan>();

        for (String row: sourceResident.getHealthPlan().split(Constants.CARRIAGE_RETURN_SEPARATOR)) {
            if (!Utils.isEmpty(row)) {
                String[] columns = row.split(Constants.TAB_SEPARATOR);

                if(columns.length == 0)
                    continue;

                ResidentHealthPlan healthPlan = new ResidentHealthPlan();

                healthPlan.setLegacyId(sourceResident.getId());
                healthPlan.setDatabaseId(databaseId);
                healthPlan.setResidentId(residentNewId);

                healthPlan.setHealthPlanName(columns[0]);

                if(columns.length > 1) {
                    healthPlan.setPolicyNumber(columns[1]);
                }

                if(columns.length > 2) {
                    healthPlan.setGroupNumber(columns[2]);
                }

                healthPlans.add(healthPlan);
            }
        }

        return healthPlans;
    }


    @Override
    public Payer.Updatable createPayerUpdatable(ResidentHealthPlan residentHealthPlan) {
        Payer.Updatable updatable = new Payer.Updatable();
        updatable.setCoverageActivityId(residentHealthPlan.getPolicyNumber());

        return updatable;
    }

    @Override
    public Participant.Updatable createParticipantUpdatable() {
        Participant.Updatable updatable = new Participant.Updatable();
        updatable.setRoleCodeId(selfRoleCode.getId());
        return updatable;
    }

    @Override
    public Payer createPayer(ResidentHealthPlan residentHealthPlan, Long legacyId, long databaseId) {
        Payer payer = new Payer();
        payer.setDatabaseId(databaseId);
        payer.setLegacyId(legacyId);
        payer.setResidentId(residentHealthPlan.getResidentId());
        payer.setUpdatable(createPayerUpdatable(residentHealthPlan));
        return payer;
    }

    @Override
    public Participant createParticipant(long personNewId, Long legacyId, long databaseId) {
        Participant participant = new Participant();
        participant.setDatabaseId(databaseId);
        participant.setLegacyId(legacyId);
        participant.setLegacyTable(ParticipantType.POLICY_TARGET.getTableName());
        participant.setPersonId(personNewId);
        participant.setUpdatable(createParticipantUpdatable());
        return participant;
    }

    @Override
    public PolicyActivity createPolicyActivity(long payerNewId, long participantNewId, Long legacyId, long databaseId,
                                               ResidentData resident, long payerOrganizationNewId, ResidentHealthPlan residentHealthPlan) {
        PolicyActivity policyActivity = new PolicyActivity();
        policyActivity.setDatabaseId(databaseId);
        policyActivity.setLegacyId(legacyId);
        policyActivity.setPayerId(payerNewId);
        policyActivity.setParticipantId(participantNewId);
        policyActivity.setUpdatable(createPolicyActivityUpdatable(resident, payerOrganizationNewId, residentHealthPlan));
        return policyActivity;
    }

    @Override
    public Organization createPayerOrganization(ResidentHealthPlan residentHealthPlan, Long legacyId, long databaseId) {
        Organization organization = new Organization();
        organization.setLegacyTable(OrganizationType.PAYER.getLegacyTableName());
        organization.setLegacyId(legacyId.toString());
        organization.setDatabaseId(databaseId);
        Organization.Updatable updatable = new Organization.Updatable();
        updatable.setName(residentHealthPlan.getHealthPlanName());
        organization.setUpdatable(updatable);
        return organization;
    }

    @Override
    public CoveragePlanDescription.Updatable createCoveragePlanDescriptionUpdatable(ResidentHealthPlan residentHealthPlan) {
        CoveragePlanDescription.Updatable updatable = new CoveragePlanDescription.Updatable();
        updatable.setText(residentHealthPlan.getHealthPlanName());
        return updatable;
    }

    @Override
    public PolicyActivity.Updatable createPolicyActivityUpdatable(ResidentData resident, long payerOrganizationNewId, ResidentHealthPlan residentHealthPlan) {
        PolicyActivity.Updatable updatable = new PolicyActivity.Updatable();
        updatable.setParticipantDateOfBirth(resident.getBirthDate());
        updatable.setPayerOrganizationId(payerOrganizationNewId);
        updatable.setParticipantMemberId(residentHealthPlan.getPolicyNumber());
        return updatable;
    }

    @Override
    public CoveragePlanDescription createCoveragePlanDescription(ResidentHealthPlan residentHealthPlan, long policyActivityId, Long legacyId, long databaseId) {
        CoveragePlanDescription coveragePlanDescription = new CoveragePlanDescription();
        coveragePlanDescription.setDatabaseId(databaseId);
        coveragePlanDescription.setLegacyId(legacyId);
        coveragePlanDescription.setPolicyActivityId(policyActivityId);
        coveragePlanDescription.setUpdatable(createCoveragePlanDescriptionUpdatable(residentHealthPlan));
        return coveragePlanDescription;
    }
}

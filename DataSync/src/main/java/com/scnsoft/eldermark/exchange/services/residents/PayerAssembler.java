package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.*;
import com.scnsoft.eldermark.framework.DatabaseInfo;

import java.util.List;

public interface PayerAssembler {
    List<ResidentHealthPlan> getResidentHealthPlans(ResidentData sourceResident, long residentNewId, long databaseId);

    Payer.Updatable createPayerUpdatable(ResidentHealthPlan residentHealthPlan);
    Payer createPayer(ResidentHealthPlan residentHealthPlan, Long legacyId, long databaseId);

    Participant.Updatable createParticipantUpdatable();
    Participant createParticipant(long personNewId, Long legacyId, long databaseId);

    Organization createPayerOrganization(ResidentHealthPlan residentHealthPlan, Long legacyId, long databaseId);

    PolicyActivity.Updatable createPolicyActivityUpdatable(ResidentData resident, long payerOrganizationNewId, ResidentHealthPlan residentHealthPlan);
    PolicyActivity createPolicyActivity(long payerNewId, long participantNewId, Long legacyId, long databaseId, ResidentData resident, long payerOrganizationNewId, ResidentHealthPlan residentHealthPlan);

    CoveragePlanDescription.Updatable createCoveragePlanDescriptionUpdatable(ResidentHealthPlan residentHealthPlan);
    CoveragePlanDescription createCoveragePlanDescription(ResidentHealthPlan residentHealthPlan, long policyActivityId, Long legacyId, long databaseId);
}

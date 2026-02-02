package com.scnsoft.eldermark.consana.sync.client.predicates;


import com.scnsoft.eldermark.consana.sync.common.entity.HieConsentPolicyType;
import com.scnsoft.eldermark.consana.sync.client.model.entities.Resident;
import com.scnsoft.eldermark.consana.sync.client.services.entities.ResidentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class ConsanaIntegrationEnabledPredicate implements Predicate<Resident> {

    final static String MC_FARLAND_CONSANA_ORG_ID = "Exchange_Meds_Repo_McFarland_Pharmacy";

    private final ResidentService residentService;

    @Autowired
    public ConsanaIntegrationEnabledPredicate(ResidentService residentService) {
        this.residentService = residentService;
    }

    @Override
    public boolean test(Resident resident) {
        if (resident == null || !Boolean.TRUE.equals(resident.getFacility().getConsanaIntegrationEnabled())) {
            return false;
        }

        if (resident.getHieConsentPolicyType() == HieConsentPolicyType.OPT_OUT) {
            return false;
        }

        if (StringUtils.isNotEmpty(resident.getHealthPartnersMemberIdentifier())) {
            return true;
        }

        if (Boolean.TRUE.equals(resident.getActive())) {
            if (MC_FARLAND_CONSANA_ORG_ID.equals(resident.getFacility().getConsanaOrgId())) {
                return true;
            }
        }

        return residentService.isPharmacyNamesAndAdmittedDateCorrect(resident);
    }
}

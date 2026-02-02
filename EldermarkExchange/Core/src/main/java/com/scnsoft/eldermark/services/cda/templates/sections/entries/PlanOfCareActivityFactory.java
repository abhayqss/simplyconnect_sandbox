package com.scnsoft.eldermark.services.cda.templates.sections.entries;

import com.scnsoft.eldermark.entity.PlanOfCareActivity;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.IVL_TS;
import org.eclipse.mdht.uml.hl7.datatypes.SXCM_TS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author phomal
 * Created on 4/25/2018.
 */
@Component
public class PlanOfCareActivityFactory {

    private final CcdCodeFactory ccdCodeFactory;

    @Autowired
    public PlanOfCareActivityFactory(CcdCodeFactory ccdCodeFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
    }


    public List<PlanOfCareActivity> parseSupplies(Resident resident, EList<Supply> supplies) {
        if (CollectionUtils.isEmpty(supplies)) {
            return Collections.emptyList();
        }

        // TODO test on real examples
        final List<PlanOfCareActivity> planOfCareActivitySupplies = new ArrayList<>();
        for (Supply supply : supplies) {
            if (!CcdParseUtils.hasContent(supply)) continue;

            final PlanOfCareActivity planOfCareActivity = new PlanOfCareActivity();
            planOfCareActivity.setDatabase(resident.getDatabase());
            planOfCareActivity.setMoodCode(supply.getMoodCode().getName());
            final SXCM_TS sxcm_ts = CcdParseUtils.getFirstNotEmptyValue(supply.getEffectiveTimes(), SXCM_TS.class);
            if (sxcm_ts instanceof IVL_TS) {
                planOfCareActivity.setEffectiveTime(CcdTransform.IVLTStoCenterDateOrTsToDate((IVL_TS) sxcm_ts));
            } else {
                planOfCareActivity.setEffectiveTime(CcdParseUtils.convertTsToDate(sxcm_ts));
            }
            planOfCareActivitySupplies.add(planOfCareActivity);
        }

        return planOfCareActivitySupplies;
    }

    public List<PlanOfCareActivity> parseSubstanceAdministrations(Resident resident, EList<SubstanceAdministration> substanceAdministrations) {
        if (CollectionUtils.isEmpty(substanceAdministrations)) {
            return Collections.emptyList();
        }

        // TODO test on real examples
        final List<PlanOfCareActivity> planOfCareActivitySubstanceAdministrations = new ArrayList<>();
        for (SubstanceAdministration substanceAdministration : substanceAdministrations) {
            if (!CcdParseUtils.hasContent(substanceAdministration)) continue;

            final PlanOfCareActivity planOfCareActivity = new PlanOfCareActivity();
            planOfCareActivity.setDatabase(resident.getDatabase());
            planOfCareActivity.setMoodCode(substanceAdministration.getMoodCode().getName());
            final SXCM_TS sxcm_ts = CcdParseUtils.getFirstNotEmptyValue(substanceAdministration.getEffectiveTimes(), SXCM_TS.class);
            if (sxcm_ts instanceof IVL_TS) {
                planOfCareActivity.setEffectiveTime(CcdTransform.IVLTStoCenterDateOrTsToDate((IVL_TS) sxcm_ts));
            } else {
                planOfCareActivity.setEffectiveTime(CcdParseUtils.convertTsToDate(sxcm_ts));
            }


            if (substanceAdministration.getConsumable() != null) {
                final ManufacturedProduct manufacturedProduct = substanceAdministration.getConsumable().getManufacturedProduct();
                if (manufacturedProduct != null) {
                    final LabeledDrug manufacturedLabeledDrug = manufacturedProduct.getManufacturedLabeledDrug();
                    if (manufacturedLabeledDrug != null) {
                        planOfCareActivity.setCode(ccdCodeFactory.convert(manufacturedLabeledDrug.getCode()));
                    }
                }
            }

            planOfCareActivitySubstanceAdministrations.add(planOfCareActivity);
        }

        return planOfCareActivitySubstanceAdministrations;
    }

    public List<PlanOfCareActivity> parseProcedures(Resident resident, EList<org.eclipse.mdht.uml.cda.Procedure> procedures) {
        if (CollectionUtils.isEmpty(procedures)) {
            return Collections.emptyList();
        }

        // TODO test on real examples
        final List<PlanOfCareActivity> planOfCareActivityProcedures = new ArrayList<>();
        for (org.eclipse.mdht.uml.cda.Procedure procedure : procedures) {
            if (!CcdParseUtils.hasContent(procedure)) continue;

            final PlanOfCareActivity planOfCareActivity = new PlanOfCareActivity();
            planOfCareActivity.setDatabase(resident.getDatabase());

            planOfCareActivity.setMoodCode(procedure.getMoodCode().getName());
            planOfCareActivity.setCode(ccdCodeFactory.convert(procedure.getCode()));
            planOfCareActivity.setEffectiveTime(CcdTransform.IVLTStoHighLowDateOrIVLTStoCenterDateOrTsToDate(
                    procedure.getEffectiveTime()).getSecond());

            planOfCareActivityProcedures.add(planOfCareActivity);
        }

        return planOfCareActivityProcedures;
    }

    public List<PlanOfCareActivity> parseEncounters(Resident resident, EList<org.eclipse.mdht.uml.cda.Encounter> encounters) {
        if (CollectionUtils.isEmpty(encounters)) {
            return Collections.emptyList();
        }
        // TODO test on real examples
        final List<PlanOfCareActivity> result = new ArrayList<>();
        for (org.eclipse.mdht.uml.cda.Encounter encounter : encounters) {
            if (!CcdParseUtils.hasContent(encounter)) continue;

            final PlanOfCareActivity planOfCareActivity = new PlanOfCareActivity();
            planOfCareActivity.setDatabase(resident.getDatabase());
            planOfCareActivity.setMoodCode(encounter.getMoodCode().getName());
            planOfCareActivity.setEffectiveTime(CcdTransform.IVLTStoHighLowDateOrIVLTStoCenterDateOrTsToDate(
                    encounter.getEffectiveTime()).getSecond());
            result.add(planOfCareActivity);
        }
        return result;
    }

    public List<PlanOfCareActivity> parseObservations(Resident resident, EList<Observation> observations) {
        if (CollectionUtils.isEmpty(observations)) {
            return Collections.emptyList();
        }

        // TODO test on real examples
        final List<PlanOfCareActivity> result = new ArrayList<>();
        for (Observation observation : observations) {
            final PlanOfCareActivity planOfCareActivity = parseObservation(resident, observation);
            if (planOfCareActivity != null) {
                result.add(planOfCareActivity);
            }
        }

        return result;
    }

    public <A extends Act> List<PlanOfCareActivity> parseActs(Resident resident, EList<A> planOfCareActivityActs) {
        if (CollectionUtils.isEmpty(planOfCareActivityActs)) {
            return Collections.emptyList();
        }
        // TODO test on real examples
        final List<PlanOfCareActivity> result = new ArrayList<>();
        for (Act ccdPlanOfCareActivityAct : planOfCareActivityActs) {
            // Plan of Care Activity Act
            final PlanOfCareActivity planOfCareActivity = parseAct(resident, ccdPlanOfCareActivityAct);
            if (planOfCareActivity != null) {
                result.add(planOfCareActivity);
            }
        }
        return result;
    }

    private PlanOfCareActivity parseObservation(Resident resident, Observation observation) {
        if (!CcdParseUtils.hasContent(observation)) return null;

        final PlanOfCareActivity planOfCareActivity = new PlanOfCareActivity();
        planOfCareActivity.setDatabase(resident.getDatabase());

        planOfCareActivity.setMoodCode(observation.getMoodCode().getName());
        planOfCareActivity.setCode(ccdCodeFactory.convert(observation.getCode()));
        planOfCareActivity.setEffectiveTime(CcdTransform.IVLTStoHighLowDateOrIVLTStoCenterDateOrTsToDate(
                observation.getEffectiveTime()).getSecond());

        return planOfCareActivity;
    }

    private PlanOfCareActivity parseAct(Resident resident, Act ccdPlanOfCareActivityAct) {
        if (!CcdParseUtils.hasContent(ccdPlanOfCareActivityAct)) {
            return null;
        }

        final PlanOfCareActivity planOfCareActivity = new PlanOfCareActivity();
        planOfCareActivity.setDatabase(resident.getDatabase());
        planOfCareActivity.setMoodCode(ccdPlanOfCareActivityAct.getMoodCode().getName());
        planOfCareActivity.setCode(ccdCodeFactory.convert(ccdPlanOfCareActivityAct.getCode()));
        planOfCareActivity.setEffectiveTime(CcdTransform.IVLTStoHighLowDateOrIVLTStoCenterDateOrTsToDate(
                ccdPlanOfCareActivityAct.getEffectiveTime()).getSecond());

        return planOfCareActivity;
    }


}

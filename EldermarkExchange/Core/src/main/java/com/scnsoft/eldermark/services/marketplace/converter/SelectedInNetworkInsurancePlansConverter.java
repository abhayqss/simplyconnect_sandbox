package com.scnsoft.eldermark.services.marketplace.converter;

import com.google.common.collect.Lists;
import com.scnsoft.eldermark.entity.marketplace.InsurancePlan;
import com.scnsoft.eldermark.entity.marketplace.Marketplace;
import com.scnsoft.eldermark.entity.phr.InNetworkInsurance;
import com.scnsoft.eldermark.entity.phr.MarketplaceInNetworkInsurancePlan;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author phomal
 *         Created on 11/28/2017.
 */
@Component
public class SelectedInNetworkInsurancePlansConverter implements Converter<Marketplace, Map<String, String[]>> {


    @Override
    public Map<String, String[]> convert(Marketplace marketplace) {
        final Map<String, List<String>> insurancePlanNames = new TreeMap<>();

        if (CollectionUtils.isNotEmpty(marketplace.getInNetworkInsurances())) {
            for (InNetworkInsurance inNetworkInsurance : marketplace.getInNetworkInsurances()) {
                if (!insurancePlanNames.containsKey(inNetworkInsurance.getDisplayName())) {
                    insurancePlanNames.put(inNetworkInsurance.getDisplayName(), new ArrayList<String>());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(marketplace.getInsurancePlans())) {
            for (InsurancePlan insurancePlan : marketplace.getInsurancePlans()) {
                if(!insurancePlanNames.containsKey(insurancePlan.getInNetworkInsurance().getDisplayName())) {
                    insurancePlanNames.put(insurancePlan.getInNetworkInsurance().getDisplayName(), new ArrayList<String>());
                }
                insurancePlanNames.get(insurancePlan.getInNetworkInsuranceId()).add(insurancePlan.getDisplayName());
            }
        }

        final Map<String, String[]> result = new LinkedHashMap<>(insurancePlanNames.size());
        for (Map.Entry<String, List<String>> entry : insurancePlanNames.entrySet()) {
            result.put(entry.getKey(), entry.getValue().toArray(new String[entry.getValue().size()]));
        }
        return result;
    }
}

package com.scnsoft.eldermark.services.marketplace.internal;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.scnsoft.eldermark.dao.marketplace.InsurancePlanDao;
import com.scnsoft.eldermark.entity.marketplace.DisplayableNamedEntity;
import com.scnsoft.eldermark.entity.marketplace.InsurancePlan;
import com.scnsoft.eldermark.shared.carecoordination.AlphabetableKeyTwoValuesDto;
import com.scnsoft.eldermark.shared.carecoordination.AlphabetableKeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyTwoValuesDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.scnsoft.eldermark.dao.marketplace.InsurancePlanDao.ORDER_BY_DISPLAY_NAME;

/**
 * @author phomal
 * Created on 12/6/2017.
 */
@Component
public class InsurancePlansDtoSupplier implements Supplier<Map<Long, List<AlphabetableKeyTwoValuesDto>>> {

    private final Supplier<Map<Long, List<AlphabetableKeyTwoValuesDto>>> memoized;

    {
        memoized = Suppliers.memoizeWithExpiration(this, 60, TimeUnit.MINUTES);
    }

    @Autowired
    private InsurancePlanDao insurancePlanDao;

    @Override
    public Map<Long, List<AlphabetableKeyTwoValuesDto>> get() {
        List<InsurancePlan> insurancePlans = insurancePlanDao.findAll(new Sort(ORDER_BY_DISPLAY_NAME));

        final Multimap<Long, InsurancePlan> groupByInsuranceId = Multimaps.index(insurancePlans, new Function<InsurancePlan, Long>() {
            @Override
            public Long apply(InsurancePlan input) {
                return input.getInNetworkInsuranceId();
            }
        });

        final Map<Long, List<AlphabetableKeyTwoValuesDto>> result = new HashMap<>();
        for (Long inNetworkInsuranceId :groupByInsuranceId.keySet()) {
            final Collection<InsurancePlan> insurancePlansForCarrier = groupByInsuranceId.get(inNetworkInsuranceId);
            final List<AlphabetableKeyTwoValuesDto> alphabetableKeyValueDtos = EntityListToDtoListConverter.convertTwoLabelAlphabetable(new ArrayList<>(insurancePlansForCarrier));
            result.put(inNetworkInsuranceId, alphabetableKeyValueDtos);
        }

        return result;
    }

    public Map<Long, List<AlphabetableKeyTwoValuesDto>> getMemoized() {
        return memoized.get();
    }

}

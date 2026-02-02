package com.scnsoft.eldermark.services.marketplace.internal;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.scnsoft.eldermark.dao.marketplace.InsurancePlanDao;
import com.scnsoft.eldermark.entity.marketplace.InsurancePlan;
import com.scnsoft.eldermark.shared.carecoordination.KeyTwoValuesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.scnsoft.eldermark.dao.marketplace.InsurancePlanDao.ORDER_BY_DISPLAY_NAME;

/**
 * @author phomal
 * Created on 12/6/2017.
 */
@Component
public class PopularInsurancePlansDtoSupplier implements Supplier<Map<Long, Collection<KeyTwoValuesDto>>> {

    private final Supplier<Map<Long, Collection<KeyTwoValuesDto>>> memoized;

    {
        memoized = Suppliers.memoizeWithExpiration(this, 60, TimeUnit.MINUTES);
    }

    @Autowired
    private InsurancePlanDao insurancePlanDao;

    @Override
    public Map<Long, Collection<KeyTwoValuesDto>> get() {
        List<InsurancePlan> insurancePlans = insurancePlanDao.findPopularItems(new Sort(ORDER_BY_DISPLAY_NAME));

        final Multimap<Long, InsurancePlan> groupByInsuranceId = Multimaps.index(insurancePlans, new Function<InsurancePlan, Long>() {
            @Override
            public Long apply(InsurancePlan input) {
                return input.getInNetworkInsuranceId();
            }
        });
        return Multimaps.transformEntries(groupByInsuranceId, new Maps.EntryTransformer<Long, InsurancePlan, KeyTwoValuesDto>() {
            @Override
            public KeyTwoValuesDto transformEntry(Long key, InsurancePlan value) {
                return new KeyTwoValuesDto(value.getId(), value.getDisplayName(), value.getInNetworkInsurance().getDisplayName());
            }
        }).asMap();
    }

    public Map<Long, Collection<KeyTwoValuesDto>> getMemoized() {
        return memoized.get();
    }

}

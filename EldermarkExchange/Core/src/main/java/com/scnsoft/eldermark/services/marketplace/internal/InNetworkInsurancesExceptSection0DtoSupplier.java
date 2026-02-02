package com.scnsoft.eldermark.services.marketplace.internal;

import com.scnsoft.eldermark.dao.phr.InNetworkInsuranceDao;
import com.scnsoft.eldermark.entity.phr.InNetworkInsurance;
import com.scnsoft.eldermark.shared.carecoordination.AlphabetableKeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.scnsoft.eldermark.dao.phr.InNetworkInsuranceDao.ORDER_BY_DISPLAY_NAME;

/**
 * @author phomal
 * Created on 11/28/2017.
 */
@Component
public class InNetworkInsurancesExceptSection0DtoSupplier extends AbstractDtoListSupplier<AlphabetableKeyValueDto> {

    @Autowired
    private InNetworkInsuranceDao inNetworkInsuranceDao;

    @Override
    public List<AlphabetableKeyValueDto> get() {
        List<InNetworkInsurance> insurances = inNetworkInsuranceDao.findItemsWithoutSection0(new Sort(Arrays.asList(ORDER_BY_DISPLAY_NAME)));
        return EntityListToDtoListConverter.convertAlphabetable(insurances);
    }

}

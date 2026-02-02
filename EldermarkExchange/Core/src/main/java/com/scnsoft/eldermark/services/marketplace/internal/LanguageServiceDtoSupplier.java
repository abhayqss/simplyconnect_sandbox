package com.scnsoft.eldermark.services.marketplace.internal;

import com.scnsoft.eldermark.dao.marketplace.LanguageServiceDao;
import com.scnsoft.eldermark.entity.marketplace.LanguageService;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.scnsoft.eldermark.dao.marketplace.LanguageServiceDao.ORDER_BY_DISPLAY_NAME;

/**
 * @author phomal
 * Created on 11/28/2017.
 */
@Component
public class LanguageServiceDtoSupplier extends AbstractDtoListSupplier {

    @Autowired
    private LanguageServiceDao languageServiceDao;

    @Override
    public List<KeyValueDto> get() {
        List<LanguageService> languageServices = languageServiceDao.findAll(new Sort(ORDER_BY_DISPLAY_NAME));
        EntityListUtils.moveItemToEnd(languageServices, "other languages");
        return EntityListToDtoListConverter.convert(languageServices);
    }

}

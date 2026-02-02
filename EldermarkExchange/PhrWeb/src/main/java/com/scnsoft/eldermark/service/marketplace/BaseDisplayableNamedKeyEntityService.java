package com.scnsoft.eldermark.service.marketplace;

import com.scnsoft.eldermark.entity.marketplace.DisplayableNamedKeyEntity;
import com.scnsoft.eldermark.service.BasePhrService;
import com.scnsoft.eldermark.web.entity.BaseDisplayableNamedKeyDto;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDisplayableNamedKeyEntityService<TARGET extends BaseDisplayableNamedKeyDto, SOURCE extends DisplayableNamedKeyEntity> extends BasePhrService {

    protected List<TARGET> transform(List<SOURCE> sourceList) {
        List<TARGET> dtos = new ArrayList<>();
        for (SOURCE source : sourceList) {
            dtos.add(transformListItem(source));
        }
        return dtos;
    }

    protected TARGET transformListItem(SOURCE source) {
        TARGET target = createNewDto();
        target.setId(source.getId());
        target.setKey(source.getKey());
        target.setName(source.getDisplayName());
        return target;
    }

    protected Integer findNumber(Integer currentNumber, Integer step, Integer maxNumber, String searchDisplayName, List<SOURCE> entities) {
        if (currentNumber > maxNumber) {
            if (step == 1) {
                return null;
            }
            return findNumber(currentNumber - step, 1, maxNumber, searchDisplayName, entities);
        }
        String currentDisplayName = entities.get(currentNumber).getDisplayName();
        if (currentDisplayName.compareToIgnoreCase(searchDisplayName) < 0) {
            return findNumber(currentNumber + step, step, maxNumber, searchDisplayName, entities);
        } else if (currentDisplayName.compareToIgnoreCase(searchDisplayName) == 0) {
            return currentNumber;
        } else {
            if (step == 1) {
                return null;
            }
            return findNumber(currentNumber - step, 1, maxNumber, searchDisplayName, entities);
        }
    }

    protected abstract TARGET createNewDto();
}

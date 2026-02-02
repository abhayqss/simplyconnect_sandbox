package com.scnsoft.eldermark.services.marketplace.internal;

import com.google.common.collect.Lists;
import com.scnsoft.eldermark.entity.marketplace.DisplayableNamedEntity;
import com.scnsoft.eldermark.entity.marketplace.DisplayablePrimaryFocusAwareEntity;
import com.scnsoft.eldermark.entity.marketplace.InsurancePlan;
import com.scnsoft.eldermark.shared.carecoordination.AlphabetableKeyTwoValuesDto;
import com.scnsoft.eldermark.shared.carecoordination.AlphabetableKeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.PrimaryFocusKeyValueDto;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author phomal
 * Created on 11/28/2017.
 */
final class EntityListToDtoListConverter {

    private EntityListToDtoListConverter() {}

    public static List<KeyValueDto> convert(List<? extends DisplayableNamedEntity> source) {
        List<KeyValueDto> result = Lists.newArrayListWithExpectedSize(CollectionUtils.size(source));
        for (DisplayableNamedEntity item : source) {
            result.add(new KeyValueDto(item.getId(), item.getDisplayName()));
        }
        return result;
    }

    public static List<PrimaryFocusKeyValueDto> convertPrimaryFocusKeyValueDto(List<? extends DisplayablePrimaryFocusAwareEntity> source) {
        List<PrimaryFocusKeyValueDto> result = Lists.newArrayListWithExpectedSize(CollectionUtils.size(source));
        for (DisplayablePrimaryFocusAwareEntity item : source) {
            result.add(new PrimaryFocusKeyValueDto(item.getId(), item.getDisplayName(),item.getPrimaryFocusId()));
        }
        return result;
    }

    public static List<AlphabetableKeyValueDto> convertAlphabetable(List<? extends DisplayableNamedEntity> source) {
        List<AlphabetableKeyValueDto> result = Lists.newArrayListWithExpectedSize(CollectionUtils.size(source));

        String firstSymbol = "";
        for (DisplayableNamedEntity item : source) {
            final String currentFirstSymbol = getFirstSymbol(item.getDisplayName());
            boolean firstInLetterSection = false;
            if (!firstSymbol.equals(currentFirstSymbol)) {
                firstSymbol  = currentFirstSymbol;
                firstInLetterSection = true;
            }
            result.add(new AlphabetableKeyValueDto(item.getId(), item.getDisplayName(), currentFirstSymbol, firstInLetterSection));
        }
        return result;
    }

    public static List<AlphabetableKeyTwoValuesDto> convertTwoLabelAlphabetable(List<? extends InsurancePlan> source) {
        List<AlphabetableKeyTwoValuesDto> result = Lists.newArrayListWithExpectedSize(CollectionUtils.size(source));

        String firstSymbol = "";
        for (InsurancePlan item : source) {
            final String currentFirstSymbol = getFirstSymbol(item.getDisplayName());
            boolean firstInLetterSection = false;
            if (!firstSymbol.equals(currentFirstSymbol)) {
                firstSymbol = currentFirstSymbol;
                firstInLetterSection = true;
            }
            result.add(new AlphabetableKeyTwoValuesDto(item.getId(), item.getDisplayName(), item.getInNetworkInsurance().getDisplayName(), currentFirstSymbol, firstInLetterSection));
        }
        return result;
    }

    private static String getFirstSymbol(String displayName) {
        if (Character.isAlphabetic(displayName.charAt(0))) {
            return String.valueOf(displayName.charAt(0));
        }
        return "#";
    }

}

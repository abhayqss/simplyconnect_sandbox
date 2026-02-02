package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.healthdata.AllergyObservationDao;
import com.scnsoft.eldermark.entity.Allergy;
import com.scnsoft.eldermark.entity.AllergyObservation;
import com.scnsoft.eldermark.entity.ReactionObservation;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.web.entity.AllergyInfoDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author phomal
 */
@Service
@Transactional(readOnly = true)
public class AllergyService extends BasePhrService {

    @Autowired
    AllergyObservationDao allergyObservationDao;

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;


    public Page<AllergyInfoDto> getUserAllergiesActive(Long userId, Pageable pageable) {
        return getUserAllergies(userId, true, false, false, pageable);
    }

    public Page<AllergyInfoDto> getUserAllergiesInactive(Long userId, Pageable pageable) {
        return getUserAllergies(userId, false, true, false, pageable);
    }

    public Page<AllergyInfoDto> getUserAllergiesResolved(Long userId, Pageable pageable) {
        return getUserAllergies(userId, false, false, true, pageable);
    }

    private Page<AllergyInfoDto> getUserAllergies(Long userId, final boolean includeActive, boolean includeInactive, boolean includeResolved,
                                                  Pageable pageable) {

        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
        Collection<Long> residentIds = getResidentIdsOrThrow(userId);
        Page<AllergyObservation> page = allergyObservationDao.listResidentAllergiesWithoutDuplicates(residentIds, includeActive, includeInactive, includeResolved,
                pageable);
        Page<AllergyInfoDto> resultingPage = page.map(new Converter<AllergyObservation, AllergyInfoDto>() {
            @Override
            public AllergyInfoDto convert(AllergyObservation source) {
                return transform(source);
            }
        });

        return resultingPage;
    }

    static AllergyInfoDto transform(AllergyObservation srcObservation) {
        AllergyInfoDto dest = new AllergyInfoDto();

        final Allergy allergy = srcObservation.getAllergy();
        dest.setProductText(srcObservation.getProductText());
        dest.setAllergyType(srcObservation.getAdverseEventTypeText());
        if (allergy.getTimeHigh() != null) {
            dest.setEndDate(allergy.getTimeHigh().getTime());
        } else if (srcObservation.getTimeHigh() != null) {
            dest.setEndDate(srcObservation.getTimeHigh().getTime());
        }

        // See Value Set Allergy/Adverse Event Type 2013‑01‑31, Id 2.16.840.1.113883.3.88.12.3221.6.2
        if (StringUtils.isNotBlank(srcObservation.getAdverseEventTypeText())) {
            switch (StringUtils.lowerCase(srcObservation.getAdverseEventTypeText())) {
                case "drug allergy":
                case "drug intolerance":
                case "propensity to adverse reactions to drug":
                    dest.setAllergenType(AllergyInfoDto.AllergenType.DRUG);
                    break;
                case "food allergy":
                case "food intolerance":
                case "propensity to adverse reactions to food":
                    dest.setAllergenType(AllergyInfoDto.AllergenType.FOOD);
                    break;
                case "environmental allergy":
                case "propensity to adverse reactions to substance":
                    dest.setAllergenType(AllergyInfoDto.AllergenType.ENVIRONMENT);
                    break;
            }
        }

        Collection<ReactionObservation> reactionObservations = srcObservation.getReactionObservations();
        List<String> reactionObservationsText = new ArrayList<>();
        CollectionUtils.collect(reactionObservations, new BeanToPropertyValueTransformer("reactionText", true), reactionObservationsText);
        dest.setReaction(StringUtils.join(reactionObservationsText, ", "));
        if (StringUtils.isBlank(dest.getReaction())) {
            dest.setReaction(null);
        }

        dest.setDataSource(DataSourceService.transform(srcObservation.getDatabase(), allergy.getResidentId()));

        return dest;
    }

}

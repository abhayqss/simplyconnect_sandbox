package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.healthdata.PregnancyObservationDao;
import com.scnsoft.eldermark.dao.healthdata.SmokingStatusObservationDao;
import com.scnsoft.eldermark.dao.healthdata.SocialHistoryObservationDao;
import com.scnsoft.eldermark.dao.healthdata.TobaccoUseDao;
import com.scnsoft.eldermark.entity.PregnancyObservation;
import com.scnsoft.eldermark.entity.SmokingStatusObservation;
import com.scnsoft.eldermark.entity.SocialHistoryObservation;
import com.scnsoft.eldermark.entity.TobaccoUse;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.service.validation.AccessibleResidentsProvider;
import com.scnsoft.eldermark.web.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional(readOnly = true)
public class SocialHistoryService extends BasePhrService {

    @Autowired
    private PregnancyObservationDao pregnancyObservationDao;

    @Autowired
    private SmokingStatusObservationDao smokingStatusObservationDao;

    @Autowired
    private SocialHistoryObservationDao socialHistoryObservationDao;

    @Autowired
    private TobaccoUseDao tobaccoUseDao;

    public SocialHistoryCountDto getSocialHistorySectionsCount(Long authorityId, AccessibleResidentsProvider accessibleResidentsProvider) {
        final Collection<Long> residentIds = accessibleResidentsProvider.getAccessibleResidentsOrThrow(authorityId, AccessRight.Code.MY_PHR);
        SocialHistoryCountDto result = new SocialHistoryCountDto();
        result.setPregnancyCount(pregnancyObservationDao.countResidentsPregnancyObservationsWithoutDuplicates(residentIds));
        result.setSmokingStatusCount(smokingStatusObservationDao.countResidentsSmokingStatusObservationsWithoutDuplicates(residentIds));
        result.setSocialHistoryCount(socialHistoryObservationDao.countResidentsSocialHistoryObservationsWithoutDuplicates(residentIds));
        result.setTobaccoUseCount(tobaccoUseDao.countResidentsTobaccoUseWithoutDuplicates(residentIds));
        return result;
    }

    public Page<PregnancyObservationInfoDto> getPregnancyObservations(Long authorityId, Pageable pageable, AccessibleResidentsProvider accessibleResidentsProvider) {
        final Collection<Long> residentIds = accessibleResidentsProvider.getAccessibleResidentsOrThrow(authorityId, AccessRight.Code.MY_PHR);
        Page<PregnancyObservation> page = pregnancyObservationDao.listResidentsPregnancyObservationsWithoutDuplicates(residentIds, pageable);
        final Page<PregnancyObservationInfoDto> resultingPage = page.map(new Converter<PregnancyObservation, PregnancyObservationInfoDto>() {
            @Override
            public PregnancyObservationInfoDto convert(PregnancyObservation pregnancyObservation) {
                return transformPregnancyListItem(pregnancyObservation);
            }
        });
        return resultingPage;
    }

    public PregnancyObservationDto getPregnancyObservation(Long pregnancyObservationId) {
        PregnancyObservation pregnancyObservation = pregnancyObservationDao.getOne(pregnancyObservationId);
        return transformPregnancy(pregnancyObservation);
    }

    public Page<SmokingStatusInfoDto> getSmokingStatuses(Long authorityId, Pageable pageable, AccessibleResidentsProvider accessibleResidentsProvider) {
        final Collection<Long> residentIds = accessibleResidentsProvider.getAccessibleResidentsOrThrow(authorityId, AccessRight.Code.MY_PHR);
        Page<SmokingStatusObservation> page = smokingStatusObservationDao.listResidentsSmokingStatusObservationsWithoutDuplicates(residentIds,pageable);
        final Page<SmokingStatusInfoDto> resultingPage = page.map(new Converter<SmokingStatusObservation, SmokingStatusInfoDto>() {
            @Override
            public SmokingStatusInfoDto convert(SmokingStatusObservation smokingStatusObservation) {
                return transformSmokingStatusListItem(smokingStatusObservation);
            }
        });
        return resultingPage;
    }

    public SmokingStatusDto getSmokingStatus(Long smokingStatusId) {
        SmokingStatusObservation smokingStatusObservation = smokingStatusObservationDao.getOne(smokingStatusId);
        return transformSmokingStatus(smokingStatusObservation);
    }

    public Page<SocialHistoryObservationInfoDto> getSocialHistoryObservations(Long authorityId, Pageable pageable, AccessibleResidentsProvider accessibleResidentsProvider) {
        final Collection<Long> residentIds = accessibleResidentsProvider.getAccessibleResidentsOrThrow(authorityId, AccessRight.Code.MY_PHR);
        Page<SocialHistoryObservation> page = socialHistoryObservationDao.listResidentsSocialHistoryObservationsWithoutDuplicates(residentIds, pageable);
        final Page<SocialHistoryObservationInfoDto> resultingPage = page.map(new Converter<SocialHistoryObservation, SocialHistoryObservationInfoDto>() {
            @Override
            public SocialHistoryObservationInfoDto convert(SocialHistoryObservation socialHistoryObservation) {
                return transformSocialHistoryObservationListItem(socialHistoryObservation);
            }
        });
        return resultingPage;
    }

    public SocialHistoryObservationDto getSocialHistoryObservation(Long socialHistoryObservationId) {
        SocialHistoryObservation socialHistoryObservation = socialHistoryObservationDao.getOne(socialHistoryObservationId);
        return transformSocialHistoryObservation(socialHistoryObservation);
    }

    public Page<TobaccoUseInfoDto> getTobaccoUses(Long authorityId, Pageable pageable, AccessibleResidentsProvider accessibleResidentsProvider) {
        final Collection<Long> residentIds = accessibleResidentsProvider.getAccessibleResidentsOrThrow(authorityId, AccessRight.Code.MY_PHR);
        Page<TobaccoUse> page = tobaccoUseDao.listResidentsTobaccoUseWithoutDuplicates(residentIds, pageable);
        final Page<TobaccoUseInfoDto> resultingPage = page.map(new Converter<TobaccoUse, TobaccoUseInfoDto>() {
            @Override
            public TobaccoUseInfoDto convert(TobaccoUse tobaccoUse) {
                return transformTobaccoUseListItem(tobaccoUse);
            }
        });
        return resultingPage;
    }

    public TobaccoUseDto getTobaccoUse(Long tobaccoUseId) {
        TobaccoUse tobaccoUse = tobaccoUseDao.getOne(tobaccoUseId);
        return transformTobaccoUse(tobaccoUse);
    }

    static PregnancyObservationDto transformPregnancy(PregnancyObservation pregnancyObservation) {
        PregnancyObservationDto result = new PregnancyObservationDto();
        result.setId(pregnancyObservation.getId());
        result.setStartDate(pregnancyObservation.getEffectiveTimeLow() != null ? pregnancyObservation.getEffectiveTimeLow().getTime() : null);
        result.setEstimatedDeliveryDate(pregnancyObservation.getEstimatedDateOfDelivery() != null ? pregnancyObservation.getEstimatedDateOfDelivery().getTime() : null);
        result.setDataSource(DataSourceService.transform(pregnancyObservation.getDatabase(), pregnancyObservation.getSocialHistory().getResident().getId()));
        return result;
    }

    static PregnancyObservationInfoDto transformPregnancyListItem(PregnancyObservation pregnancyObservation) {
        PregnancyObservationInfoDto result = new PregnancyObservationInfoDto();
        result.setId(pregnancyObservation.getId());
        result.setEffectiveTimeLow(pregnancyObservation.getEffectiveTimeLow() != null ? pregnancyObservation.getEffectiveTimeLow().getTime() : null);
        return result;
    }

    static SmokingStatusDto transformSmokingStatus(SmokingStatusObservation smokingStatusObservation) {
        SmokingStatusDto result = new SmokingStatusDto();
        result.setId(smokingStatusObservation.getId());
        result.setSmokingStatus(smokingStatusObservation.getValue() != null ? smokingStatusObservation.getValue().getDisplayName() : null);
        result.setStartDate(smokingStatusObservation.getEffectiveTimeLow() != null ? smokingStatusObservation.getEffectiveTimeLow().getTime() : null);
        result.setDataSource(DataSourceService.transform(smokingStatusObservation.getDatabase(), smokingStatusObservation.getSocialHistory().getResident().getId()));
        return result;
    }

    static SmokingStatusInfoDto transformSmokingStatusListItem(SmokingStatusObservation smokingStatusObservation) {
        SmokingStatusInfoDto result = new SmokingStatusInfoDto();
        result.setId(smokingStatusObservation.getId());
        result.setEffectiveTimeLow(smokingStatusObservation.getEffectiveTimeLow() != null ? smokingStatusObservation.getEffectiveTimeLow().getTime() : null);
        return result;
    }

    static SocialHistoryObservationDto transformSocialHistoryObservation(SocialHistoryObservation socialHistoryObservation) {
        //TODO check if the fields are correct
        SocialHistoryObservationDto result = new SocialHistoryObservationDto();
        result.setId(socialHistoryObservation.getId());
        result.setType(socialHistoryObservation.getType() != null ? socialHistoryObservation.getType().getDisplayName() : null);
        result.setText(socialHistoryObservation.getFreeText());
        result.setValue(socialHistoryObservation.getFreeTextValue());
        result.setDataSource(DataSourceService.transform(socialHistoryObservation.getDatabase(), socialHistoryObservation.getSocialHistory().getResident().getId()));
        return result;
    }

    static SocialHistoryObservationInfoDto transformSocialHistoryObservationListItem(SocialHistoryObservation socialHistoryObservation) {
        SocialHistoryObservationInfoDto result = new SocialHistoryObservationInfoDto();
        result.setId(socialHistoryObservation.getId());
        result.setType(socialHistoryObservation.getType() != null ? socialHistoryObservation.getType().getDisplayName() : null);
        return result;
    }

    static TobaccoUseDto transformTobaccoUse(TobaccoUse tobaccoUse) {
        TobaccoUseDto result = new TobaccoUseDto();
        result.setId(tobaccoUse.getId());
        result.setType(tobaccoUse.getValue() != null ? tobaccoUse.getValue().getDisplayName() : null);
        result.setStartDate(tobaccoUse.getEffectiveTimeLow() != null ? tobaccoUse.getEffectiveTimeLow().getTime() : null);
        result.setDataSource(DataSourceService.transform(tobaccoUse.getDatabase(), tobaccoUse.getSocialHistory().getResident().getId()));
        return result;
    }

    static TobaccoUseInfoDto transformTobaccoUseListItem(TobaccoUse tobaccoUse) {
        TobaccoUseInfoDto result = new TobaccoUseInfoDto();
        result.setId(tobaccoUse.getId());
        result.setType(tobaccoUse.getValue() != null ? tobaccoUse.getValue().getDisplayName() : null);
        result.setEffectiveTime(tobaccoUse.getEffectiveTimeLow() != null ? tobaccoUse.getEffectiveTimeLow().getTime() : null);
        return result;
    }
}

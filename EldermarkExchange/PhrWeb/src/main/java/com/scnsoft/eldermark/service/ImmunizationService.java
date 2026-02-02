package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.healthdata.ImmunizationDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.web.entity.ImmunizationInfoDto;
import com.scnsoft.eldermark.web.entity.PeriodDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static com.scnsoft.eldermark.dao.healthdata.ImmunizationDao.ORDER_BY_NAME;
import static com.scnsoft.eldermark.dao.healthdata.ImmunizationDao.ORDER_BY_START_DATE_DESC;

/**
 * @author phomal
 */
@Service
@Transactional(readOnly = true)
public class ImmunizationService extends BasePhrService {

    @Autowired
    ImmunizationDao immunizationDao;

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;


    public Page<ImmunizationInfoDto> getUserImmunizations(Long userId, Pageable pageable) {

        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
        Collection<Long> activeResidentIds = getResidentIdsOrThrow(userId);

        final Sort sort = new Sort(ORDER_BY_START_DATE_DESC, ORDER_BY_NAME);
        final Pageable pageableWithSort;
        if (pageable == null) {
            pageableWithSort = new PageRequest(0, Integer.MAX_VALUE, sort);
        } else {
            pageableWithSort = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }

        Page<Immunization> page = immunizationDao.listResidentImmunizationsWithoutDuplicates(activeResidentIds, pageableWithSort);
        final Page<ImmunizationInfoDto> resultingPage = page.map(new Converter<Immunization, ImmunizationInfoDto>() {
            @Override
            public ImmunizationInfoDto convert(Immunization source) {
                return transform(source);
            }
        });

        return resultingPage;
    }

    public ImmunizationInfoDto getUserImmunization(Long userId, Long immunizationId) {

        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);

        Immunization src = immunizationDao.findOne(immunizationId);
        if (src == null) {
            throw new PhrException(PhrExceptionType.IMMUNIZATION_NOT_FOUND);
        }

        // validate association
        Collection<Long> activeResidentIds = getResidentIdsOrThrow(userId);
        if (!activeResidentIds.contains(src.getResident().getId())) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        return transform(src);
    }

    static ImmunizationInfoDto transform(Immunization src) {
        ImmunizationInfoDto dest = new ImmunizationInfoDto();

        dest.setId(src.getId());
        ImmunizationMedicationInformation immunizationMedicationInformation = src.getImmunizationMedicationInformation();
        if (immunizationMedicationInformation != null) {
            dest.setImmunizationName(immunizationMedicationInformation.getText());
        }
        dest.setAdministeredBy(src.getText());
        if (src.getDoseQuantity() != null) {
            dest.setDoseQuantity(Double.valueOf(src.getDoseQuantity()));
        }
        dest.setDoseUnit(src.getDoseUnits());
        dest.setRepeat(src.getRepeatNumber());

        PeriodDto period = new PeriodDto();
        dest.setPeriod(period);
        if (src.getImmunizationStarted() != null) {
            period.setStartDate(src.getImmunizationStarted().getTime());
            period.setStartDateStr(DATE_TIME_FORMAT.format(src.getImmunizationStarted()));
        }
        if (src.getImmunizationStopped() != null) {
            period.setEndDate(src.getImmunizationStopped().getTime());
            period.setEndDateStr(DATE_TIME_FORMAT.format(src.getImmunizationStopped()));
        }

        ReactionObservation srcReactionObservation = src.getReactionObservation();
        if (srcReactionObservation != null) {
            dest.setReaction(StringUtils.trimToNull(srcReactionObservation.getReactionText()));
        }

        dest.setRefusal(src.getRefusal());
        ImmunizationRefusalReason srcImmunizationRefusalReason = src.getImmunizationRefusalReason();
        if (srcImmunizationRefusalReason != null) {
            CcdCode refusalCode = srcImmunizationRefusalReason.getCode();
            if (refusalCode != null) {
                dest.setRefusalReason(refusalCode.getDisplayName());
            }
        }

        CcdCode route = src.getRoute();
        if (route != null) {
            dest.setRoute(route.getDisplayName());
        }
        CcdCode site = src.getSite();
        if (site != null) {
            dest.setSite(site.getDisplayName());
        }
        Instructions instructions = src.getInstructions();
        if (instructions != null) {
            dest.setInstructions(instructions.getText());
        }

        for (Indication indication : src.getIndications()) {
            CcdCode code = indication.getCode();
            CcdCode value = indication.getValue();
            if (code != null && value != null) {
                dest.putIndicationsItem(code.getDisplayName(), value.getDisplayName());
            }
        }

        dest.setStatus(StringUtils.capitalize(src.getStatusCode()));
        dest.setDataSource(DataSourceService.transform(src.getDatabase(), src.getResident().getId()));
        return dest;
    }

}

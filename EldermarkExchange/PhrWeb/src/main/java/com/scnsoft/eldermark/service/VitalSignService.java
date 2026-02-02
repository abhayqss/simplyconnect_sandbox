package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.VitalSignReferenceInfoDao;
import com.scnsoft.eldermark.dao.healthdata.VitalSignObservationDao;
import com.scnsoft.eldermark.dao.projections.VitalSignTypeAndDate;
import com.scnsoft.eldermark.dao.projections.VitalSignTypeAndObservation;
import com.scnsoft.eldermark.entity.VitalSignObservation;
import com.scnsoft.eldermark.entity.phr.VitalSignReferenceInfo;
import com.scnsoft.eldermark.entity.phr.VitalSignType;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.web.entity.DateDto;
import com.scnsoft.eldermark.shared.web.entity.ReportPeriod;
import com.scnsoft.eldermark.web.entity.VitalSignObservationDto;
import com.scnsoft.eldermark.web.entity.VitalSignObservationReport;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import com.scnsoft.eldermark.shared.utils.PeriodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import static com.scnsoft.eldermark.dao.healthdata.VitalSignObservationDao.ORDER_BY_DATE;
import static com.scnsoft.eldermark.dao.healthdata.VitalSignObservationDao.ORDER_BY_DATE_DESC;

/**
 * @author averazub
 * @author phomal
 * Created by averazub on 1/6/2017.
 */
@Service
@Transactional(readOnly = true)
public class VitalSignService extends BasePhrService  {

    Logger logger = Logger.getLogger(VitalSignService.class.getName());

    @Autowired
    VitalSignObservationDao vitalSignObservationDao;

    @Autowired
    com.scnsoft.eldermark.dao.VitalSignDao legacyVitalSignDao;

    @Autowired
    VitalSignReferenceInfoDao vitalSignReferenceInfoDao;

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;

    private final ConcurrentMap<VitalSignType, String> vitalSignReferenceInfo = new ConcurrentHashMap<>();

    @PostConstruct
    void initCache() {
        if (!vitalSignReferenceInfo.isEmpty()) {
            return;
        }

        List<VitalSignReferenceInfo> infoList = vitalSignReferenceInfoDao.findAll();
        for (VitalSignReferenceInfo info: infoList) {
            final VitalSignType vitalSignType = VitalSignType.getByCode(info.getCode());
            if (vitalSignType != null) {
                vitalSignReferenceInfo.put(vitalSignType, info.getReferenceInfo());
            } else {
                logger.warning("Unsupported Vital Sign with code = " + info.getCode() + ". Reference info is not available for this vital sign.");
            }
        }
    }

    public VitalSignObservationReport getVitalSignDetails(Long userId, VitalSignType vitalSignType, ReportPeriod reportPeriod,
                                                          Integer maxResults, Integer page) {

        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
        Collection<Long> activeResidentIds = getResidentIdsOrThrow(userId);

        Pair<Date, Date> reportRange = reportPeriod==null ? null : PeriodUtils.getPeriodRange(reportPeriod, page);
        if (reportRange == null) {
            reportRange = new Pair<>(null, new Date());
        }
        final Pageable pageable = new PageRequest(0, maxResults, new Sort(ORDER_BY_DATE_DESC));
        List<VitalSignObservation> vitalSigns = vitalSignObservationDao.listResidentVitalSignObservationsWithoutDuplicates(activeResidentIds,
                vitalSignType.code(), reportRange.getFirst(), reportRange.getSecond(), pageable);

        VitalSignObservationReport report = new VitalSignObservationReport(vitalSignType.toString(), vitalSignType.displayName());
        report.setResults(new ArrayList<VitalSignObservationDto>());
        if (vitalSigns.size()>0) report.setUnit(vitalSigns.get(0).getUnit());
        for (VitalSignObservation src: vitalSigns) {
            VitalSignObservationDto dest = transform(src);
            report.getResults().add(dest);
        }
        return report;
    }

    public Map<String, VitalSignObservationDto> getVitalSignLatestResults(Long userId) {

        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
        Collection<Long> activeResidentIds = getResidentIdsOrThrow(userId);

        /*
        List<VitalSignObservation> vsoList = vitalSignObservationDao.listLatestResidentVitalSignObservations(activeResidentIds);
        Map<String, VitalSignObservation> latestResults = new HashMap<>();
        for (VitalSignObservation vso : vsoList) {
            final String key = vso.getResultTypeCode().getCode();
            if (!latestResults.containsKey(key) || latestResults.get(key).getEffectiveTime().before(vso.getEffectiveTime())) {
                latestResults.put(key, vso);
            }
        }
        */

        // legacy DAO is used here for running a native SQL query (better performance)
        Map<String, VitalSignObservation> latestResults = legacyVitalSignDao.listLatestResidentVitalSigns(activeResidentIds);
        Map<String, VitalSignObservationDto> result = new HashMap<>();

        for (VitalSignType type: VitalSignType.values()) {
            if (latestResults.containsKey(type.code())) {
                final VitalSignObservation observation = latestResults.get(type.code());
                result.put(type.name(), transform(observation, true));
            } else {
                result.put(type.name(), null);
            }
        }

        return result;
    }

    public Map<String, DateDto> getVitalSignEarliestMeasurementDates(Long userId) {

        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
        Collection<Long> activeResidentIds = getResidentIdsOrThrow(userId);

        List<VitalSignTypeAndDate> vsoList = vitalSignObservationDao.listEarliestResidentVitalSignObservationDates(activeResidentIds);
        Map<String, Date> earliestResults = new HashMap<>();
        for (VitalSignTypeAndDate vso : vsoList) {
            earliestResults.put(vso.getType(), vso.getDate());
        }
        Map<String, DateDto> result = new HashMap<>();

        for (VitalSignType type: VitalSignType.values()) {
            if (earliestResults.containsKey(type.code())) {
                Date date = earliestResults.get(type.code());
                DateDto dateDto = new DateDto();
                if (date != null) {
                    dateDto.setDateTime(date.getTime());
                    dateDto.setDateTimeStr(DATE_TIME_FORMAT.format(date));
                }
                result.put(type.name(), dateDto);
            } else {
                result.put(type.name(), null);
            }
        }

        return result;
    }

    public VitalSignObservationDto getVitalSignEarliestMeasurement(Long userId, VitalSignType vitalSignType, String unit) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
        Collection<Long> activeResidentIds = getResidentIdsOrThrow(userId);

        final Pageable earliestTop1 = new PageRequest(0, 1, new Sort(ORDER_BY_DATE));

        List<VitalSignObservation> earliestResults = vitalSignObservationDao.listResidentVitalSignObservations(activeResidentIds, vitalSignType.code(), earliestTop1);
        VitalSignObservation earliestResult = CollectionUtils.isEmpty(earliestResults) ? null : earliestResults.get(0);

        return transform(earliestResult, true);
    }

    private static VitalSignObservation transform(VitalSignTypeAndObservation vsoProjection) {
        VitalSignObservation vso = new VitalSignObservation();
        vso.setValue(vsoProjection.getValue());
        vso.setUnit(vsoProjection.getUnit());
        vso.setEffectiveTime(vsoProjection.getDate());
        return null;
    }

    private VitalSignObservationDto transform(VitalSignObservation src) {
        return transform(src, false);
    }

    private VitalSignObservationDto transform(VitalSignObservation src, boolean withUnit) {
        VitalSignObservationDto dest = new VitalSignObservationDto();
        if (src == null) {
            return dest;
        }
        if (src.getEffectiveTime() != null) {
            dest.setDateTime(src.getEffectiveTime().getTime());
            dest.setDateTimeStr(DATE_TIME_FORMAT.format(src.getEffectiveTime()));
        }
        if (withUnit) {
            dest.setUnit(src.getUnit());
        }
        dest.setValue(src.getValue());
        return dest;
    }

    public String getVitalSignReferenceInfo(VitalSignType type) {
        return vitalSignReferenceInfo.get(type);
    }

    public static Map<VitalSignType, String> getVitalSigns() {
        Map<VitalSignType, String> map = new HashMap<>();
        for (VitalSignType vitalSignType : VitalSignType.values()) {
            map.put(vitalSignType, vitalSignType.displayName());
        }
        return map;
    }

}

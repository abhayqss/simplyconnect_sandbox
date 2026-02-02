package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.VitalSign;
import com.scnsoft.eldermark.entity.VitalSignObservation;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @deprecated Transition to Spring Data repositories is recommended. Use {@link com.scnsoft.eldermark.dao.healthdata.VitalSignObservationDao VitalSignObservationDao} instead.
 */
public interface VitalSignDao extends ResidentAwareDao<VitalSign> {
    List<VitalSignObservation> listResidentVitalSigns(Long residentId, String vitalSignTypeCcdCode, Pair<Date, Date> periodRange,
                                                      Integer maxResults);

    List<VitalSignObservation> listResidentVitalSigns(Collection<Long> residentIds, String vitalSignTypeCcdCode, Pair<Date, Date> periodRange,
                                                      Integer maxResults);

    /**
     * @return Map of latest vital sign observations,
     * where key is Vital Sign CCD Code, value is the latest vital sign observation
     */
    Map<String, VitalSignObservation> listLatestResidentVitalSigns(Collection<Long> residentIds);

    /**
     * @return Map of earliest vital sign dates,
     * where key is Vital Sign CCD Code, value is earliest date of vital sign observation
     */
    Map<String, Date> listEarliestResidentVitalSigns(Long residentId);

    /**
     * @return Map of earliest vital sign dates,
     * where key is Vital Sign CCD Code, value is earliest date of vital sign observation
     */
    Map<String, Date> listEarliestResidentVitalSigns(Collection<Long> residentIds);

    /**
     * @return The earliest vital sign observation
     */
    VitalSignObservation getEarliestResidentVitalSign(Collection<Long> residentIds, String vitalSignTypeCcdCode);

}

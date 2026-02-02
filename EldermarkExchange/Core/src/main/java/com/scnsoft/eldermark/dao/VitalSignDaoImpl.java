package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.VitalSign;
import com.scnsoft.eldermark.entity.VitalSignObservation;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

@Repository
public class VitalSignDaoImpl extends ResidentAwareDaoImpl<VitalSign> implements VitalSignDao {

    public VitalSignDaoImpl() {
        super(VitalSign.class);
    }

    @Override
    public List<VitalSignObservation> listResidentVitalSigns(Long residentId, String vitalSignTypeCcdCode, Pair<Date, Date> periodRange,
                                                             Integer maxResults) {
        return listResidentVitalSigns(Collections.singletonList(residentId), vitalSignTypeCcdCode, periodRange, maxResults);
    }

    @Override
    public List<VitalSignObservation> listResidentVitalSigns(Collection<Long> residentIds, String vitalSignTypeCcdCode, Pair<Date, Date> periodRange,
                                                             Integer maxResults) {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("select vso from VitalSignObservation vso INNER JOIN vso.vitalSign vs INNER JOIN vso.resultTypeCode ccd ");
        queryStr.append("WHERE vs.resident.id IN :residentIds AND ccd.code=:vitalSignTypeCcdCode ");
        if (periodRange == null) {
            periodRange = new Pair<Date, Date>(null, new Date());
        }

        if (periodRange.getFirst() != null) {
            queryStr.append(" AND vso.effectiveTime>=:fromDate ");
        }
        if (periodRange.getSecond() != null) {
            queryStr.append(" AND vso.effectiveTime<=:toDate ");
        }

        queryStr.append(" ORDER BY vso.effectiveTime DESC ");
        TypedQuery<VitalSignObservation> query = entityManager.createQuery(queryStr.toString(), VitalSignObservation.class);
        query.setParameter("residentIds", residentIds);
        query.setParameter("vitalSignTypeCcdCode", vitalSignTypeCcdCode);

        if (periodRange.getFirst() != null) {
            query.setParameter("fromDate", periodRange.getFirst());
        }
        if (periodRange.getSecond() != null) {
            query.setParameter("toDate", periodRange.getSecond());
        }

        if (maxResults != null) {
            query.setMaxResults(maxResults);
        }
        return query.getResultList();
    }

    @Override
    public Map<String, VitalSignObservation> listLatestResidentVitalSigns(Collection<Long> residentIds) {
        Map<String, VitalSignObservation> result = new HashMap<String, VitalSignObservation>();
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT q.* FROM (SELECT ccd.code, vso.effective_time, vso.value, vso.unit, row_number() ");
        queryStr.append("OVER (PARTITION BY ccd.code ORDER BY vso.effective_time DESC) AS rn FROM VitalSignObservation vso ");
        queryStr.append("INNER JOIN VitalSign vs ON vs.id = vso.vital_sign_id INNER JOIN CcdCode ccd ON ccd.id = vso.result_type_code_id ");
        queryStr.append("WHERE vs.resident_id IN :residentIds) AS q WHERE rn = 1");

        Query query = entityManager.createNativeQuery(queryStr.toString());
//        Query query = entityManager.createNamedQuery("vitalSignObservation.latestResidentVitalSigns");
        query.setParameter("residentIds", residentIds);
        List<Object[]> resultSet = query.getResultList();
        for (Object[] resultItem : resultSet) {
            VitalSignObservation observation = new VitalSignObservation();
            observation.setEffectiveTime((Date) resultItem[1]);
            observation.setValue((Double) resultItem[2]);
            observation.setUnit((String) resultItem[3]);
            result.put((String) resultItem[0], observation);
        }

        return result;
    }

    @Override
    public Map<String, Date> listEarliestResidentVitalSigns(Long residentId) {
        return listEarliestResidentVitalSigns(Collections.singletonList(residentId));
    }

    @Override
    public Map<String, Date> listEarliestResidentVitalSigns(Collection<Long> residentIds) {
        Map<String, Date> result = new HashMap<String, Date>();
        Query query = entityManager.createNamedQuery("vitalSignObservation.listEarliestResidentVitalSigns");
        query.setParameter("residentIds", residentIds);
        List<Object[]> resultSet = query.getResultList();
        for (Object[] resultItem : resultSet) {
            result.put((String) resultItem[0], (Date) resultItem[1]);
        }
        return result;
    }

    @Override
    public VitalSignObservation getEarliestResidentVitalSign(Collection<Long> residentIds, String vitalSignTypeCcdCode) {
        TypedQuery<VitalSignObservation> query = entityManager.createNamedQuery("vitalSignObservation.getEarliestResidentVitalSign", VitalSignObservation.class);
        query.setParameter("residentIds", residentIds);
        query.setParameter("ccdCode", vitalSignTypeCcdCode);
        query.setMaxResults(1);

        return query.getSingleResult();
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (VitalSign vitalSign : this.listByResidentId(residentId)) {
            this.delete(vitalSign);
            ++count;
        }

        return count;
    }
}

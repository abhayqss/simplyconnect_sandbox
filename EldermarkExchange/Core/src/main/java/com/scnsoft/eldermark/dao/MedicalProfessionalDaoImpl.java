package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.ResidentMedProfessional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class MedicalProfessionalDaoImpl extends ResidentAwareDaoImpl<ResidentMedProfessional> implements MedicalProfessionalDao {

    public MedicalProfessionalDaoImpl() {
        super(ResidentMedProfessional.class);
    }

    @Override
    public List<ResidentMedProfessional> listByResidentId(Long residentId) {
        TypedQuery<ResidentMedProfessional> query = entityManager.createQuery(
                "select resMedProfessional from ResidentMedProfessional resMedProfessional" +
                        " join resMedProfessional.resident res" +
                        " where res.id = :residentId order by resMedProfessional.rank",
                ResidentMedProfessional.class);
        query.setParameter("residentId", residentId);
        return query.getResultList();
    }

    @Override
    public List<ResidentMedProfessional> listByResidentIds(List<Long> residentIds, Pageable pageable) {
        TypedQuery<ResidentMedProfessional> query = entityManager.createQuery(
                "select resMedProfessional from ResidentMedProfessional resMedProfessional" +
                        " join resMedProfessional.resident res" +
                        " where res.id IN :residentIds order by resMedProfessional.rank",
                ResidentMedProfessional.class);
        query.setParameter("residentIds", residentIds);
        applyPageable(query, pageable);

        return query.getResultList();
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (ResidentMedProfessional medProfessional : this.listByResidentId(residentId)) {
            this.delete(medProfessional);
            ++count;
        }

        return count;
    }

}

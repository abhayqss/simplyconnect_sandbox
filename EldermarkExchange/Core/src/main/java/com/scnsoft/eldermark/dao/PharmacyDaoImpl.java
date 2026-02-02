package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.ResidentPharmacy;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;

@Repository
public class PharmacyDaoImpl extends ResidentAwareDaoImpl<ResidentPharmacy> implements PharmacyDao {

    public PharmacyDaoImpl() {
        super(ResidentPharmacy.class);
    }

    public List<Organization> listPharmaciesAsOrganization(Long residentId) {
        TypedQuery<Organization> query = entityManager.createQuery(
                "select org from ResidentPharmacy resPharmacy" +
                        " left join resPharmacy.organization org" +
                        " left join resPharmacy.resident res" +
                        " where res.id = :residentId order by resPharmacy.rank",
                Organization.class);
        query.setParameter("residentId", residentId);
        return query.getResultList();
    }

    @Override
    public List<Organization> listPharmaciesAsOrganization(Collection<Long> residentIds) {
        TypedQuery<Organization> query = entityManager.createQuery(
                "select org from ResidentPharmacy resPharmacy" +
                        " left join resPharmacy.organization org" +
                        " left join resPharmacy.resident res" +
                        " where res.id IN :residentIds order by resPharmacy.rank",
                Organization.class);
        query.setParameter("residentIds", residentIds);
        return query.getResultList();
    }

}

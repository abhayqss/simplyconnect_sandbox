package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.DataEnterer;
import com.scnsoft.eldermark.dao.exceptions.MultipleEntitiesFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;

@Repository
public class DataEntererDaoImpl extends ResidentAwareDaoImpl<DataEnterer> implements DataEntererDao {

    public DataEntererDaoImpl() {
        super(DataEnterer.class);
    }

    @Override
    public DataEnterer getCcdDataEnterer(Long residentId) {
        TypedQuery<DataEnterer> query = entityManager.createQuery("select d from DataEnterer d where d.resident.id = :residentId",
                                                                   DataEnterer.class);
        query.setParameter("residentId", residentId);

        try {
            return query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            return null;
        } catch (javax.persistence.NonUniqueResultException e) {
            throw new MultipleEntitiesFoundException(String.format("Resident [id = %d] has several DataEnterers", residentId), e);
        }
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        final DataEnterer dataEnterer = getCcdDataEnterer(residentId);
        if (dataEnterer == null) {
            return 0;
        } else {
            delete(dataEnterer);
            return 1;
        }
    }

}

package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.ClientMedicationCount;
import com.scnsoft.eldermark.entity.medication.ClientMedication;
import com.scnsoft.eldermark.entity.medication.ClientMedication_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class CustomClientMedicationDaoImpl implements CustomClientMedicationDao {

    private final EntityManager entityManager;

    @Autowired
    public CustomClientMedicationDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<ClientMedicationCount> countGroupedByStatus(Specification<ClientMedication> specification) {
        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createQuery(ClientMedicationCount.class);
        var root = crq.from(ClientMedication.class);

        crq.multiselect(root.get(ClientMedication_.status), cb.count(root.get(ClientMedication_.id)));
        crq.where(specification.toPredicate(root, crq, cb));
        crq.groupBy(root.get(ClientMedication_.status));

        var typed = entityManager.createQuery(crq);
        return typed.getResultList();
    }
}

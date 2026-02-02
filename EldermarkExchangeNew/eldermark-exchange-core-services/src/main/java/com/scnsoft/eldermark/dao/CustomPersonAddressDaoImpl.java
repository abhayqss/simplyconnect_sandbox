package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.Person_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class CustomPersonAddressDaoImpl implements CustomPersonAddressDao {

    private final EntityManager entityManager;

    @Autowired
    public CustomPersonAddressDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Map<Long, List<PersonAddress>> findAllByClientIdIn(List<Long> clientIds) {
        var cb = entityManager.getCriteriaBuilder();

        var crq = cb.createTupleQuery();
        crq.distinct(true);

        var root = crq.from(Client.class);

        var personJoin = JpaUtils.getOrCreateJoin(root, Client_.person);
        var addresses = JpaUtils.getOrCreateListJoin(personJoin, Person_.addresses);

        crq.multiselect(root.get(Client_.id).alias(Client_.ID), addresses.alias(Person_.ADDRESSES));
        crq.where(root.get(Client_.id).in(clientIds));

        var typed = entityManager.createQuery(crq);

        var resultList = typed.getResultList();
        return resultList.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(Client_.ID, Long.class),
                        Collectors.mapping(tuple -> tuple.get(Person_.ADDRESSES, PersonAddress.class),
                                Collectors.toList())
                ));

    }
}

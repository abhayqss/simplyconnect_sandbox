package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.CommunityCareTeamMember;
import com.scnsoft.eldermark.entity.CommunityCareTeamMember_;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.Person_;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class CustomPersonTelecomDaoImpl implements CustomPersonTelecomDao {

    private final EntityManager entityManager;

    @Autowired
    public CustomPersonTelecomDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Map<Long, List<PersonTelecom>> findAllByClientIdIn(List<Long> clientIds) {
        var cb = entityManager.getCriteriaBuilder();

        var crq = cb.createTupleQuery();
        crq.distinct(true);

        var root = crq.from(Client.class);

        var personJoin = JpaUtils.getOrCreateJoin(root, Client_.person);
        var addresses = JpaUtils.getOrCreateListJoin(personJoin, Person_.telecoms);

        crq.multiselect(root.get(Client_.id).alias(Client_.ID), addresses.alias(Person_.TELECOMS));
        crq.where(root.get(Client_.id).in(clientIds));

        var typed = entityManager.createQuery(crq);

        var resultList = typed.getResultList();
        return resultList.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(Client_.ID, Long.class),
                        Collectors.mapping(tuple -> tuple.get(Person_.TELECOMS, PersonTelecom.class),
                                Collectors.toList())
                ));
    }

    @Override
    public Map<Long, List<PersonTelecom>> findClientIdPersonTelecomsByCtmEmployeeIdIn(List<Long> employeeIds) {
        var cb = entityManager.getCriteriaBuilder();

        var crq = cb.createTupleQuery();
        crq.distinct(true);

        var root = crq.from(ClientCareTeamMember.class);

        var employeeJoin = JpaUtils.getOrCreateJoin(root, ClientCareTeamMember_.employee);

        var personJoin = JpaUtils.getOrCreateJoin(employeeJoin, Employee_.person);
        var addresses = JpaUtils.getOrCreateListJoin(personJoin, Person_.telecoms);

        crq.multiselect(root.get(ClientCareTeamMember_.clientId).alias(ClientCareTeamMember_.CLIENT_ID), addresses.alias(Person_.TELECOMS));
        crq.where(root.get(ClientCareTeamMember_.employeeId).in(employeeIds));

        var typed = entityManager.createQuery(crq);

        var resultList = typed.getResultList();
        return resultList.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(ClientCareTeamMember_.CLIENT_ID, Long.class),
                        Collectors.mapping(tuple -> tuple.get(Person_.TELECOMS, PersonTelecom.class),
                                Collectors.toList())
                ));
    }

    @Override
    public Map<Long, List<PersonTelecom>> findCommunityIdPersonTelecomsByCtmEmployeeIdIn(List<Long> employeeIds) {
        var cb = entityManager.getCriteriaBuilder();

        var crq = cb.createTupleQuery();
        crq.distinct(true);

        var root = crq.from(CommunityCareTeamMember.class);

        var employeeJoin = JpaUtils.getOrCreateJoin(root, CommunityCareTeamMember_.employee);

        var personJoin = JpaUtils.getOrCreateJoin(employeeJoin, Employee_.person);
        var addresses = JpaUtils.getOrCreateListJoin(personJoin, Person_.telecoms);

        crq.multiselect(root.get(CommunityCareTeamMember_.communityId).alias(CommunityCareTeamMember_.COMMUNITY_ID), addresses.alias(Person_.TELECOMS));
        crq.where(root.get(CommunityCareTeamMember_.employeeId).in(employeeIds));

        var typed = entityManager.createQuery(crq);

        var resultList = typed.getResultList();
        return resultList.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(CommunityCareTeamMember_.COMMUNITY_ID, Long.class),
                        Collectors.mapping(tuple -> tuple.get(Person_.TELECOMS, PersonTelecom.class),
                                Collectors.toList())
                ));
    }
}

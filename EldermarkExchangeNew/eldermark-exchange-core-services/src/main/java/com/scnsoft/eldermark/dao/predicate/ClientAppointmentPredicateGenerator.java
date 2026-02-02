package com.scnsoft.eldermark.dao.predicate;

import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment_;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class ClientAppointmentPredicateGenerator {

    public Predicate byCreatorOrServiceProviderIds(Collection<Long> employeeIds, Root<ClientAppointment> root, CriteriaBuilder criteriaBuilder) {
        var predicates = new ArrayList<Predicate>();
        predicates.add(root.get(ClientAppointment_.creatorId).in(employeeIds));
        var serviceProviderJoin = JpaUtils.getOrCreateJoin(root, ClientAppointment_.serviceProviders, JoinType.LEFT);
        predicates.add(serviceProviderJoin.get(Employee_.ID).in(employeeIds));
        return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
    }

    public Predicate withExternalServiceProvider(Root<ClientAppointment> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(root.get(ClientAppointment_.isExternalProviderServiceProvider), true);
    }

    public Predicate withNoServiceProviders(Root<ClientAppointment> root) {
        var serviceProviderJoin = JpaUtils.getOrCreateJoin(root, ClientAppointment_.serviceProviders, JoinType.LEFT);
        return serviceProviderJoin.get(Employee_.ID).isNull();
    }

}

package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.entity.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class PersonTelecomSpecificationGenerator {

    public Specification<PersonTelecom> byEmployeeIdIn(Collection<Long> employeeIds) {
        return (root, query, criteriaBuilder) -> {
            var personJoin = JpaUtils.getOrCreateJoin(root, PersonTelecom_.person);
            var employeeJoin = JpaUtils.getOrCreateJoin(personJoin, Person_.employee);
            return criteriaBuilder.in(employeeJoin.get(Employee_.ID)).value(employeeIds);
        };
    }

    public Specification<PersonTelecom> byCode(PersonTelecomCode code) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get(PersonTelecom_.useCode), code.toString());
    }
}

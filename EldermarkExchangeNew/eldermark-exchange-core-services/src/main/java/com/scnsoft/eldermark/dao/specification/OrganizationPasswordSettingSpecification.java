package com.scnsoft.eldermark.dao.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.entity.password.OrganizationPasswordSettings;
import com.scnsoft.eldermark.entity.password.PasswordSettingsType;

@Component
public class OrganizationPasswordSettingSpecification {

    public Specification<OrganizationPasswordSettings> byOrganizationIdAndType(Long organizationId,
                                                                               PasswordSettingsType passwordSettingsType) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (organizationId != null)
                predicates.add(criteriaBuilder.equal(root.<Long>get("organizationId"), organizationId));

            if (passwordSettingsType != null)
                predicates.add(criteriaBuilder.equal(root.join("passwordSettings").<String>get("passwordSettingsType"),
                        passwordSettingsType));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

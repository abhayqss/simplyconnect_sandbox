package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.CodeSystem;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.CcdCode_;
import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;
import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation_;
import com.scnsoft.eldermark.entity.document.ccd.Problem_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class ProblemObservationSpecificationGenerator {

    @Autowired
    private ClientSpecificationGenerator clientSpecificationGenerator;

    public <T extends IdNameAware> Specification<ProblemObservation> byAccessibleClientsInCommunitiesCreatedBeforeOrWithoutDateCreatedActiveUntilDate
            (PermissionFilter permissionFilter, Collection<T> communities, Instant createdDate, Instant activeDate) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var accessibleClients = SpecificationUtils.subquery(Client.class,
                    criteriaQuery,
                    clientRoot ->
                            clientSpecificationGenerator.accessibleClientsInCommunitiesCreatedBeforeOrWithoutDateCreatedActiveUntilDate(permissionFilter, communities, createdDate, activeDate)
                                    .toPredicate(clientRoot, criteriaQuery, criteriaBuilder));
            return root.get(ProblemObservation_.problem).get(Problem_.clientId).in(accessibleClients);
        };
    }

    public Specification<ProblemObservation> byCodeSystemAndCode(CodeSystem codeSystem, Collection<String> codes) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var problemCodeJoin = root.join(ProblemObservation_.problemCode, JoinType.LEFT);
            var translationsJoin = root.join(ProblemObservation_.translations, JoinType.LEFT);
            var predicates = new ArrayList<Predicate>();
            var codeSystemNames = codeSystem.getNames();
            var codeSystemOid = codeSystem.getOid();

            predicates.add(criteriaBuilder.and(problemCodeJoin.get(CcdCode_.code).in(codes), criteriaBuilder.equal(problemCodeJoin.get(CcdCode_.codeSystemName), codeSystemOid)));
            predicates.add(criteriaBuilder.and(root.get(ProblemObservation_.problemIcdCode).in(codes), root.get(ProblemObservation_.problemIcdCodeSet).in(codeSystemNames)));
            predicates.add(criteriaBuilder.and(translationsJoin.get(CcdCode_.code).in(codes), criteriaBuilder.equal(translationsJoin.get(CcdCode_.codeSystemName), codeSystemOid)));

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
}

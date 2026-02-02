package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.entity.ValueSet_;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.CcdCode_;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.JoinType;

@Component
public class CcdCodeSpecificationGenerator {

    public Specification<CcdCode> byDisplayNameLike(String displayName) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (StringUtils.isEmpty(displayName)) {
                return criteriaBuilder.and();
            }
            return criteriaBuilder.like(root.get(CcdCode_.displayName), SpecificationUtils.wrapWithWildcards(displayName));
        };
    }

    public Specification<CcdCode> byValueSet(ValueSetEnum valueSetEnum) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);
            return criteriaBuilder.or(
                    criteriaBuilder.equal(root.get(CcdCode_.valueSet), valueSetEnum.getOid()),
                    criteriaBuilder.in(root.join(CcdCode_.valueSets, JoinType.LEFT).get(ValueSet_.oid)).value(valueSetEnum.getOid())
            );
        };
    }

    public Specification<CcdCode> byCode(String code) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (StringUtils.isEmpty(code)) {
                return criteriaBuilder.or();
            }
            return criteriaBuilder.equal(root.get(CcdCode_.code), code);
        };
    }
}

package com.scnsoft.eldermark.hl7v2.dao.specification;

import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.CcdCode_;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

class HL7v2SpecificationUtils {

    static Predicate compareCcdCode(Path<CcdCode> ccdCodePath, CcdCode ccdCode, CriteriaBuilder criteriaBuilder) {
        if (ccdCode == null) {
            return criteriaBuilder.isNull(ccdCodePath);
        }

        return criteriaBuilder.and(
                criteriaBuilder.equal(ccdCodePath.get(CcdCode_.code), ccdCode.getCode()),
                criteriaBuilder.equal(ccdCodePath.get(CcdCode_.codeSystem), ccdCode.getCodeSystem())
        );
    }

    static Predicate compareString(Path<String> stringPath, String string, CriteriaBuilder criteriaBuilder) {
        if (string == null) {
            return criteriaBuilder.isNull(stringPath);
        }

        return criteriaBuilder.equal(stringPath, string);
    }

}

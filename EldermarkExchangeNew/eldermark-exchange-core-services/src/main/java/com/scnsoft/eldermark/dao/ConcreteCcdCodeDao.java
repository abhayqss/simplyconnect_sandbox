package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.document.ccd.ConcreteCcdCode;
import com.scnsoft.eldermark.entity.document.ccd.ConcreteCcdCode_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.NonUniqueResultException;
import java.util.stream.Collectors;

public interface ConcreteCcdCodeDao extends AppJpaRepository<ConcreteCcdCode, Long> {
    default ConcreteCcdCode getCcdCode(String code, String codeSystem) {
        return getCcdCode(code, codeSystem, null);
    }

    default ConcreteCcdCode getCcdCode(String code, String codeSystem, String valueSet) {
        var results = findAll((root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(ConcreteCcdCode_.code), code),
                        criteriaBuilder.equal(root.get(ConcreteCcdCode_.codeSystem), codeSystem))
        );

        // if there're multiple results, try to refine search
        if (results.size() > 1 && StringUtils.isNotEmpty(valueSet)) {
            results = results.stream()
                    //todo take into account ValueSet entity
                    .filter(c -> valueSet.equals(c.getValueSet()))
                    .collect(Collectors.toList());

            if (results.size() > 1) {
                throw new NonUniqueResultException();
            }
        }
        if (results.size() == 0) {
            return null;
        }
        return results.get(0);

    }


}

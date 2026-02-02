package com.scnsoft.eldermark.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.function.Function;

public interface PageNumberDao<ENTITY, SELECTOR> {

    Long findPageNumber(Function<Root<ENTITY>, Path<SELECTOR>> itemSelectorPath,
                        SELECTOR itemSelectorValue,
                        Specification<ENTITY> listSpecification,
                        Class<ENTITY> entityClass,
                        int pageSize, Sort sort);
}

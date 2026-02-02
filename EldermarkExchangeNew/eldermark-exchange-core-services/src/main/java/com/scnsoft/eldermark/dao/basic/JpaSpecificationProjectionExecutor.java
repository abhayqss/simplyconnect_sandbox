package com.scnsoft.eldermark.dao.basic;

import com.scnsoft.eldermark.dao.basic.evaluated.params.EvaluatedPropertyParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@NoRepositoryBean
public interface JpaSpecificationProjectionExecutor<T> extends JpaSpecificationExecutor<T> {

    <P> List<P> findAll(Specification<T> specification, Class<P> projectionClass);

    <P> Page<P> findAll(Specification<T> specification, Class<P> projectionClass, Pageable pageable);

    <P> List<P> findAll(Specification<T> specification, Class<P> projectionClass, Sort sort);

    <P> List<P> findAll(Specification<T> specification, Class<P> projectionClass, Sort sort, Integer limit);

    <P> List<P> findAll(Specification<T> specification, Class<P> projectionClass, Sort sort, Integer limit, Integer offset);

    <P> Page<P> findAll(Specification<T> specification, Class<P> projectionClass,
                        Map<String, EvaluatedPropertyParams> evaluatedPropertiesParams, Pageable pageable);

    <P> List<P> findAll(Specification<T> specification, Class<P> projectionClass,
                        Map<String, EvaluatedPropertyParams> evaluatedPropertiesParams, Sort sort, Integer limit, Integer offset);

    <P> Optional<P> findFirst(Specification<T> specification, Class<P> projectionClass);

    <P> Optional<P> findFirst(Specification<T> specification, Class<P> projectionClass, Sort sort);

    /**
     *
     *
     * @param specificationBuilder
     * @param projectionClass
     * @param <P>
     * @param <Q>
     * @return
     */

    /**
     * Loads data in batches in case there is a possibility that there will be more than 2100 parameters in sql query.
     * Typical use case is IN clause.
     *
     * @param specificationBuilder specification builder for a single batch
     * @param inputList            List with potential size > 2000 which will be splitted to batches
     * @param projectionClass      projection class
     * @return
     */
    <P, Q> List<P> findAllInBatches(Function<List<Q>, Specification<T>> specificationBuilder, List<Q> inputList,
                                    Class<P> projectionClass);
}

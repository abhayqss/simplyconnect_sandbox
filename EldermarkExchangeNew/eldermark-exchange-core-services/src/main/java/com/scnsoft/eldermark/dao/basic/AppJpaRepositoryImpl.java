package com.scnsoft.eldermark.dao.basic;

import com.scnsoft.eldermark.dao.basic.evaluated.params.EvaluatedPropertyParams;
import com.scnsoft.eldermark.dao.basic.evaluated.processor.EvaluatedPropertyProcessor;
import com.scnsoft.eldermark.util.StreamUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AppJpaRepositoryImpl<ENTITY, ID extends Serializable>
        extends SimpleJpaRepository<ENTITY, ID>
        implements AppJpaRepository<ENTITY, ID>,
        ProjectionFactoryAware,
        RepositoryInformationAware {

    private final EntityManager entityManager;

    //SQL server supports up to 2100 parameters. Leaving room for possible additional parameters in batch specifications
    private static final int BATCH_SIZE = 2000;

    private ProjectionFactory projectionFactory;
    private RepositoryInformation repositoryInformation;
    private final JpaEntityInformation<ENTITY, ID> entityInformation;

    /**
     * @noinspection SpringJavaInjectionPointsAutowiringInspection
     */
    public AppJpaRepositoryImpl(JpaEntityInformation<ENTITY, ID>
                                        entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        this.entityInformation = entityInformation;
    }

    @Override
    public void setProjectionFactory(ProjectionFactory projectionFactory) {
        this.projectionFactory = projectionFactory;
    }

    @Override
    public void setRepositoryInformation(RepositoryInformation information) {
        this.repositoryInformation = information;
    }

    @Override
    public <P> List<P> findAll(Specification<ENTITY> specification, Class<P> projectionClass) {
        return findAll(specification, projectionClass, (Sort) null);
    }

    @Override
    public <P> List<P> findAll(Specification<ENTITY> spec, Class<P> projectionClass, Sort sort, Integer limit) {
        return findAll(spec, projectionClass, sort, limit, null);
    }

    @Override
    public <P> Page<P> findAll(Specification<ENTITY> spec, Class<P> projectionClass, Pageable pageable) {
        return findAll(spec, projectionClass, Collections.emptyMap(), pageable);
    }

    @Override
    public <P> Page<P> findAll(Specification<ENTITY> spec, Class<P> projectionClass,
                               Map<String, EvaluatedPropertyParams> evaluatedPropertiesParams, Pageable pageable) {
        if (pageable.isUnpaged()) {
            var result = findAll(spec, projectionClass, evaluatedPropertiesParams, pageable.getSort(), null, null);
            return new PageImpl<>(result);
        } else {
            var result = findAll(
                    spec,
                    projectionClass,
                    evaluatedPropertiesParams,
                    pageable.getSort(),
                    pageable.getPageSize(),
                    (int) pageable.getOffset()
            );
            return PageableExecutionUtils.getPage(
                    result,
                    pageable,
                    () -> executeCountQuery(getCountQuery(spec, getDomainClass()))
            );
        }
    }

    @Override
    public <P> List<P> findAll(Specification<ENTITY> specification, Class<P> projectionClass, Sort sort) {
        return findAll(specification, projectionClass, sort, null);
    }

    @Override
    public <P> List<P> findAll(
            Specification<ENTITY> specification,
            Class<P> projectionClass,
            Sort sort,
            Integer limit,
            Integer offset
    ) {
        return findAll(specification, projectionClass, Collections.emptyMap(), sort, limit, offset);
    }

    @Override
    public <P> Optional<P> findFirst(Specification<ENTITY> specification, Class<P> projectionClass) {
        return findFirst(specification, projectionClass, null);
    }

    @Override
    public <P> Optional<P> findFirst(Specification<ENTITY> specification, Class<P> projectionClass, Sort sort) {
        return findAll(specification, projectionClass, sort, 1).stream().findFirst();
    }

    @Override
    public <P> List<P> findAll(
            Specification<ENTITY> specification,
            Class<P> projectionClass,
            Map<String, EvaluatedPropertyParams> evaluatedPropertiesParams,
            Sort sort,
            Integer limit,
            Integer offset
    ) {
        var cb = entityManager.getCriteriaBuilder();

        if (projectionClass.equals(getDomainClass())) {
            var query = cb.createQuery(projectionClass);
            var root = query.from(getDomainClass());
            query.where(specification.toPredicate(root, query, cb));

            if (sort != null && sort.isSorted()) {
                query.orderBy(QueryUtils.toOrders(sort, root, cb));
            }

            var typedQuery = entityManager.createQuery(query);


            if (limit != null) {
                typedQuery.setMaxResults(limit);
            }

            if (offset != null) {
                typedQuery.setFirstResult(offset);
            }

            return typedQuery.getResultList();
        }

        var query = cb.createTupleQuery();
        var root = query.from(getDomainClass());

        query.where(specification.toPredicate(root, query, cb));

        var retType = new AppProjectionReturnedType(projectionClass, getDomainClass(), projectionFactory);

        List<Selection<?>> selections;
        if (query.isDistinct()) {
            selections = ProjectionUtils.createSelections(retType, root, query, cb, evaluatedPropertiesParams);
        } else {
            selections = ProjectionUtils.createSelections(retType, root, query, cb, entityInformation.getIdAttributeNames(), evaluatedPropertiesParams);
        }
        query = query.multiselect(selections);

        if (sort != null && sort.isSorted()) {
            addSort(evaluatedPropertiesParams, sort, cb, query, root, retType);
        }

        var typedQuery = entityManager.createQuery(query);

        if (limit != null) {
            typedQuery.setMaxResults(limit);
        }

        if (offset != null) {
            typedQuery.setFirstResult(offset);
        }

        var result = typedQuery.getResultList();

        class currentMethodResolver {
        }

        return ProjectionUtils.processResult(currentMethodResolver.class.getEnclosingMethod(),
                retType, result, repositoryInformation, projectionFactory);

    }

    public void addSort(Map<String, EvaluatedPropertyParams> evaluatedPropertiesParams, Sort sort, CriteriaBuilder cb, CriteriaQuery<Tuple> query, Root<ENTITY> root, AppProjectionReturnedType retType) {
        var evaluatedProps = retType.getEvaluatedProperties()
                .stream()
                .collect(StreamUtils.toMapOfUniqueKeysAndThen(InputProperty::getAlias, InputProperty::getEvaluatedPropertyProcessor));

        List<Order> jpaOrders;

        if (evaluatedProps.isEmpty()) {
            jpaOrders = QueryUtils.toOrders(sort, root, cb);
        } else {
            var springOrders = new ArrayList<Sort.Order>();
            jpaOrders = new ArrayList<>();

            for (var order : sort) {
                if (evaluatedProps.containsKey(order.getProperty())) {
                    jpaOrders.addAll(toJpaOrders(springOrders, root, cb));
                    springOrders.clear();

                    var evaluatedPropertyOrder = getEvaluatedPropertyOrder(
                            order,
                            root,
                            query,
                            cb,
                            evaluatedProps.get(order.getProperty()),
                            evaluatedPropertiesParams.getOrDefault(order.getProperty(), null));

                    jpaOrders.add(evaluatedPropertyOrder);
                } else {
                    springOrders.add(order);
                }
            }
            jpaOrders.addAll(toJpaOrders(springOrders, root, cb));
        }
        query.orderBy(jpaOrders);
    }

    public javax.persistence.criteria.Order getEvaluatedPropertyOrder(Sort.Order order,
                                                                      Root<ENTITY> root,
                                                                      CriteriaQuery<Tuple> query,
                                                                      CriteriaBuilder cb,
                                                                      EvaluatedPropertyProcessor evaluatedPropertyProcessor,
                                                                      EvaluatedPropertyParams evaluatedPropertyParams) {
        var evaluatedPropertyExpression = evaluatedPropertyProcessor.createExpression(
                root,
                query,
                cb,
                order.getProperty(),
                evaluatedPropertyParams
        );

        return order.isAscending() ?
                cb.asc(evaluatedPropertyExpression) :
                cb.desc(evaluatedPropertyExpression);
    }

    private List<javax.persistence.criteria.Order> toJpaOrders(List<Sort.Order> orders, Root<ENTITY> root, CriteriaBuilder cb) {
        if (CollectionUtils.isNotEmpty(orders)) {
            var subSort = Sort.by(orders);
            return QueryUtils.toOrders(subSort, root, cb);
        }
        return Collections.emptyList();
    }

    @Override
    public <P, Q> List<P> findAllInBatches(Function<List<Q>, Specification<ENTITY>> specificationBuilder,
                                           List<Q> inputList, Class<P> projectionClass) {


        return IntStream.iterate(0, i -> i < inputList.size(), i -> i + BATCH_SIZE)
                .mapToObj(i -> inputList.subList(i, Math.min(i + BATCH_SIZE, inputList.size())))
                .map(specificationBuilder)
                .map(batchSpecification -> findAll(batchSpecification, projectionClass))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public <T> Optional<T> findById(ID id, Class<T> projection) {
        if (entityInformation.hasCompositeId()) {
            throw new UnsupportedOperationException("Specification projection 'findById' is currently not supported for entities with composite keys");
        }
        var resultList = findAll(
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(entityInformation.getIdAttribute()), id),
                projection);

        return resultList.stream().findFirst();
    }

    @Override
    public <T> List<T> findByIdIn(Collection<ID> ids, Class<T> projection) {
        if (entityInformation.hasCompositeId()) {
            throw new UnsupportedOperationException("Specification projection 'findByIdIn' is currently not supported for entities with composite keys");
        }
        return findAll(
                (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get(entityInformation.getIdAttribute().getName())).value(ids),
                projection);
    }

    /**
     * Implementation was taken from {@link SimpleJpaRepository#executeCountQuery}
     *
     * @see SimpleJpaRepository#executeCountQuery
     */
    private static long executeCountQuery(TypedQuery<Long> query) {
        Assert.notNull(query, "TypedQuery must not be null!");

        List<Long> totals = query.getResultList();
        long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }

    @Override
    public boolean exists(Specification<ENTITY> specification) {
        var cb = entityManager.getCriteriaBuilder();

        var query = cb.createQuery(Integer.class);
        var root = query.from(getDomainClass());
        query.where(specification.toPredicate(root, query, cb));
        query.select(cb.literal(1));

        var typed = entityManager.createQuery(query);
        typed.setMaxResults(1);
        return !typed.getResultList().isEmpty();
    }
}

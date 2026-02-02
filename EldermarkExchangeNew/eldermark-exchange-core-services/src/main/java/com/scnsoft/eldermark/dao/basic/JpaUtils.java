package com.scnsoft.eldermark.dao.basic;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static javax.persistence.metamodel.Attribute.PersistentAttributeType.*;

public final class JpaUtils {
    private JpaUtils() {
    }

    private static final Map<Attribute.PersistentAttributeType, Class<? extends Annotation>> ASSOCIATION_TYPES;

    static {
        Map<Attribute.PersistentAttributeType, Class<? extends Annotation>> persistentAttributeTypes = new HashMap<>();
        persistentAttributeTypes.put(ONE_TO_ONE, OneToOne.class);
        persistentAttributeTypes.put(ONE_TO_MANY, null);
        persistentAttributeTypes.put(MANY_TO_ONE, ManyToOne.class);
        persistentAttributeTypes.put(MANY_TO_MANY, null);
        persistentAttributeTypes.put(ELEMENT_COLLECTION, null);

        ASSOCIATION_TYPES = Collections.unmodifiableMap(persistentAttributeTypes);
    }

    /**
     * [copypaste] from {@link org.springframework.data.jpa.repository.query.QueryUtils}
     */
    static Expression<Object> toExpressionRecursively(Path<Object> path, PropertyPath property) {

        Path<Object> result = path.get(property.getSegment());
        return property.hasNext() ? toExpressionRecursively(result, property.next()) : result;
    }

    public static <T> Expression<T> toExpressionRecursively(From<?, ?> from, PropertyPath property,
                                                            boolean isForSelection) {
        return toExpressionRecursively(from, property, isForSelection, null);
    }

    /**
     * [copypaste] from {@link org.springframework.data.jpa.repository.query.QueryUtils}
     * <p>
     * We also allow to specify which join type to use. For example, inner join is needed for fetching collection properties.
     * If join type not provided, we use left join just as spring does.
     *
     * @param from
     * @param property
     * @param isForSelection
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Expression<T> toExpressionRecursively(From<?, ?> from, PropertyPath property,
                                                            boolean isForSelection,
                                                            JoinType joinType) {

        Bindable<?> propertyPathModel;
        Bindable<?> model = from.getModel();
        String segment = property.getSegment();

        if (model instanceof ManagedType) {

            /*
             *  Required to keep support for EclipseLink 2.4.x. TODO: Remove once we drop that (probably Dijkstra M1)
             *  See: https://bugs.eclipse.org/bugs/show_bug.cgi?id=413892
             */
            propertyPathModel = (Bindable<?>) ((ManagedType<?>) model).getAttribute(segment);
        } else {
            propertyPathModel = from.get(segment).getModel();
        }

        if (requiresOuterJoin(propertyPathModel, model instanceof PluralAttribute, !property.hasNext(), isForSelection)
                && !isAlreadyFetched(from, segment)) {
            Join<?, ?> join = getOrCreateJoin(from, segment, joinType);
            return (Expression<T>) (property.hasNext() ? toExpressionRecursively(join, property.next(), isForSelection)
                    : join);
        } else {
            Path<Object> path = from.get(segment);
            return (Expression<T>) (property.hasNext() ? toExpressionRecursively(path, property.next()) : path);
        }
    }

    /**
     * [copypaste] from {@link org.springframework.data.jpa.repository.query.QueryUtils}
     * Returns whether the given {@code propertyPathModel} requires the creation of a join. This is the case if we find a
     * optional association.
     *
     * @param propertyPathModel may be {@literal null}.
     * @param isPluralAttribute is the attribute of Collection type?
     * @param isLeafProperty    is this the final property navigated by a {@link PropertyPath}?
     * @param isForSelection    is the property navigated for the selection part of the query?
     * @return whether an outer join is to be used for integrating this attribute in a query.
     */
    private static boolean requiresOuterJoin(@Nullable Bindable<?> propertyPathModel, boolean isPluralAttribute,
                                             boolean isLeafProperty, boolean isForSelection) {

        if (propertyPathModel == null && isPluralAttribute) {
            return true;
        }

        if (!(propertyPathModel instanceof Attribute)) {
            return false;
        }

        Attribute<?, ?> attribute = (Attribute<?, ?>) propertyPathModel;

        if (!ASSOCIATION_TYPES.containsKey(attribute.getPersistentAttributeType())) {
            return false;
        }

        // if this path is an optional one to one attribute navigated from the not owning side we also need an explicit
        // outer join to avoid https://hibernate.atlassian.net/browse/HHH-12712 and
        // https://github.com/eclipse-ee4j/jpa-api/issues/170
        boolean isInverseOptionalOneToOne = Attribute.PersistentAttributeType.ONE_TO_ONE == attribute.getPersistentAttributeType()
                && StringUtils.hasText(getAnnotationProperty(attribute, "mappedBy", ""));

        // if this path is part of the select list we need to generate an explicit outer join in order to prevent Hibernate
        // to use an inner join instead.
        // see https://hibernate.atlassian.net/browse/HHH-12999.
        if (isLeafProperty && !isForSelection && !attribute.isCollection() && !isInverseOptionalOneToOne) {
            return false;
        }

        return getAnnotationProperty(attribute, "optional", true);
    }

    /**
     * [copypaste] from {@link org.springframework.data.jpa.repository.query.QueryUtils}
     *
     * @param attribute
     * @param propertyName
     * @param defaultValue
     * @param <T>
     * @return
     */
    private static <T> T getAnnotationProperty(Attribute<?, ?> attribute, String propertyName, T defaultValue) {

        Class<? extends Annotation> associationAnnotation = ASSOCIATION_TYPES.get(attribute.getPersistentAttributeType());

        if (associationAnnotation == null) {
            return defaultValue;
        }

        Member member = attribute.getJavaMember();

        if (!(member instanceof AnnotatedElement)) {
            return defaultValue;
        }

        Annotation annotation = AnnotationUtils.getAnnotation((AnnotatedElement) member, associationAnnotation);
        return annotation == null ? defaultValue : (T) AnnotationUtils.getValue(annotation, propertyName);
    }

    /**
     * [copypaste] from {@link org.springframework.data.jpa.repository.query.QueryUtils}
     * Return whether the given {@link From} contains a fetch declaration for the attribute with the given name.
     *
     * @param from      the {@link From} to check for fetches.
     * @param attribute the attribute name to check.
     * @return
     */
    private static boolean isAlreadyFetched(From<?, ?> from, String attribute) {

        for (Fetch<?, ?> fetch : from.getFetches()) {

            boolean sameName = fetch.getAttribute().getName().equals(attribute);

            if (sameName && fetch.getJoinType().equals(JoinType.LEFT)) {
                return true;
            }
        }

        return false;
    }

    /**
     * [copypaste] from {@link org.springframework.data.jpa.repository.query.QueryUtils}
     * Returns an existing join for the given attribute if one already exists or creates a new one if not.
     * Our modification is that we allow to specify join type. If not specified, LEFT is used.
     *
     * @param from      the {@link From} to get the current joins from.
     * @param attribute the {@link Attribute} to look for in the current joins.
     * @return will never be {@literal null}.
     */
    private static Join<?, ?> getOrCreateJoin(From<?, ?> from, String attribute, JoinType joinType) {
        if (joinType == null) {
            joinType = JoinType.LEFT;
        }
        for (Join<?, ?> join : from.getJoins()) {

            boolean sameName = join.getAttribute().getName().equals(attribute);

            if (sameName && join.getJoinType().equals(joinType)) {
                return join;
            }
        }

        return from.join(attribute, joinType);
    }

    @SuppressWarnings("unchecked")
    public static <Z, X extends Z, Y> Join<Z, Y> getOrCreateJoin(From<?, ? super X> from, Attribute<? super X, Y> attribute) {
        return getOrCreateJoin(from, attribute, null, false);
    }

    @SuppressWarnings("unchecked")
    public static <Z, X extends Z, Y> ListJoin<Z, Y> getOrCreateListJoin(From<?, ? super X> from, ListAttribute<? super X, Y> attribute) {
        return getOrCreateListJoin(from, attribute, null);
    }

    @SuppressWarnings("unchecked")
    public static <Z, X extends Z, Y> SetJoin<Z, Y> getOrCreateSetJoin(From<?, ? super X> from, SetAttribute<? super X, Y> attribute) {
        return getOrCreateSetJoin(from, attribute, null);
    }

    //our own analog of getOrCreateJoin which checks join types provided by user
    @SuppressWarnings("unchecked")
    public static <Z, X extends Z, Y> Join<Z, Y> getOrCreateJoin(From<?, ? super X> from, Attribute<? super X, Y> attribute,
                                                                 JoinType joinType) {
        return getOrCreateJoin(from, attribute, joinType, joinType != null);
    }

    @SuppressWarnings("unchecked")
    public static <Z, X extends Z, Y> Join<Z, Y> getOrCreateJoin(From<?, ? super X> from, Attribute<? super X, Y> attribute,
                                                                 JoinType joinType, boolean compareExistingJoinType) {
        return (Join<Z, Y>) getJoin(from, attribute, compareExistingJoinType ? joinType : null).orElseGet(
                () -> from.join(attribute.getName(), joinType == null ? JoinType.INNER : joinType));
    }

    @SuppressWarnings("unchecked")
    public static <Z, X extends Z, Y> ListJoin<Z, Y> getOrCreateListJoin(From<?, ? super X> from, ListAttribute<? super X, Y> attribute,
                                                                         JoinType joinType) {
        boolean shouldCompareJoinType = joinType != null;

        for (Join<? super X, ?> join : from.getJoins()) {
            if (join instanceof ListJoin) {
                boolean sameName = join.getAttribute().equals(attribute);
                if (sameName && (!shouldCompareJoinType || join.getJoinType().equals(joinType))) {
                    return (ListJoin<Z, Y>) join;
                }
            }
        }

        return from.joinList(attribute.getName(), shouldCompareJoinType ? joinType : JoinType.INNER);
    }

    @SuppressWarnings("unchecked")
    public static <Z, X extends Z, Y> SetJoin<Z, Y> getOrCreateSetJoin(From<?, ? super X> from, SetAttribute<? super X, Y> attribute,
                                                                       JoinType joinType) {
        boolean shouldCompareJoinType = joinType != null;

        for (Join<? super X, ?> join : from.getJoins()) {
            if (join instanceof SetJoin) {
                boolean sameName = join.getAttribute().equals(attribute);
                if (sameName && (!shouldCompareJoinType || join.getJoinType().equals(joinType))) {
                    return (SetJoin<Z, Y>) join;
                }
            }
        }

        return from.joinSet(attribute.getName(), shouldCompareJoinType ? joinType : JoinType.INNER);
    }

    @SuppressWarnings("unchecked")
    public static <Z, X extends Z, Y> Optional<Join<Z, Y>> getJoin(From<?, ? super X> from, Attribute<? super X, Y> attribute) {
        return getJoin(from, attribute, null);
    }

    @SuppressWarnings("unchecked")
    public static <Z, X extends Z, Y> Optional<Join<Z, Y>> getJoin(From<?, ? super X> from, Attribute<? super X, Y> attribute,
                                                                   JoinType joinType) {
        boolean shouldCompareJoinType = joinType != null;

        for (Join<? super X, ?> join : from.getJoins()) {

            boolean sameName = join.getAttribute().equals(attribute);

            if (sameName && (!shouldCompareJoinType || join.getJoinType().equals(joinType))) {
                return Optional.of((Join<Z, Y>) join);
            }
        }

        return Optional.empty();
    }

}

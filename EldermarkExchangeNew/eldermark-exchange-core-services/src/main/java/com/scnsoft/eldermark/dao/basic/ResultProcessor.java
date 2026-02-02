package com.scnsoft.eldermark.dao.basic;

import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.domain.Slice;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.ReturnedType;
import org.springframework.data.repository.util.ReactiveWrapperConverters;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Stream;

/**
 * Inspired by Spring's {@link org.springframework.data.repository.query.ResultProcessor}.
 *
 * We use AppProjectionReturnedType instead of {@link org.springframework.data.repository.query.ReturnedType}
 * We also pass it to constructor in comparison to Spring's ResultProcessor, which creates it inside constructor.
 *
 */
public class ResultProcessor {

    private final QueryMethod method;
    private final ProjectingConverter converter;

    private final AppProjectionReturnedType type;

    private ResultProcessor(QueryMethod method, ProjectingConverter converter, AppProjectionReturnedType type) {
        this.method = method;
        this.converter = converter;
        this.type = type;
    }

//    /**
//     * Creates a new {@link org.springframework.data.repository.query.ResultProcessor} from the given {@link QueryMethod} and {@link ProjectionFactory}.
//     *
//     * @param method  must not be {@literal null}.
//     * @param factory must not be {@literal null}.
//     */
//    public ResultProcessor(QueryMethod method, ProjectionFactory factory, Class<?> domainClass) {
//        this(method, factory, domainClass, method.getReturnedObjectType());
//    }

    /**
     * Creates a new {@link org.springframework.data.repository.query.ResultProcessor} for the given {@link QueryMethod}, {@link ProjectionFactory} and type.
     *
     * @param method  must not be {@literal null}.
     * @param factory must not be {@literal null}.
     * @param type    must not be {@literal null}.
     */
    public ResultProcessor(QueryMethod method, ProjectionFactory factory, AppProjectionReturnedType type) {

        Assert.notNull(method, "QueryMethod must not be null!");
        Assert.notNull(factory, "ProjectionFactory must not be null!");
        Assert.notNull(type, "Type must not be null!");

        this.method = method;
        this.type = type;
        this.converter = new ProjectingConverter(this.type, factory);
    }

//    /**
//     * Returns a new {@link org.springframework.data.repository.query.ResultProcessor} with a new projection type obtained from the given {@link ParameterAccessor}.
//     *
//     * @param accessor must not be {@literal null}.
//     * @return
//     */
//    public ResultProcessor withDynamicProjection(ParameterAccessor accessor) {
//
//        Assert.notNull(accessor, "Parameter accessor must not be null!");
//
//        Class<?> projection = accessor.findDynamicProjection();
//
//        return projection == null //
//                ? this //
//                : withType(projection);
//    }

    /**
     * Returns the {@link ReturnedType}.
     *
     * @return
     */
    public AppProjectionReturnedType getReturnedType() {
        return type;
    }

    /**
     * Post-processes the given query result.
     *
     * @param source can be {@literal null}.
     * @return
     */
    @Nullable
    public <T> T processResult(@Nullable Object source) {
        return processResult(source, ResultProcessor.NoOpConverter.INSTANCE);
    }

    /**
     * Post-processes the given query result using the given preparing {@link Converter} to potentially prepare collection
     * elements.
     *
     * @param source             can be {@literal null}.
     * @param preparingConverter must not be {@literal null}.
     * @return
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T processResult(@Nullable Object source, Converter<Object, Object> preparingConverter) {

        if (source == null || type.isInstance(source) || !type.isProjecting()) {
            return (T) source;
        }

        Assert.notNull(preparingConverter, "Preparing converter must not be null!");

        ResultProcessor.ChainingConverter converter = ResultProcessor.ChainingConverter.of(type.getReturnedType(), preparingConverter).and(this.converter);

        if (source instanceof Slice && method.isPageQuery() || method.isSliceQuery()) {
            return (T) ((Slice<?>) source).map(converter::convert);
        }

        if (source instanceof Collection && method.isCollectionQuery()) {

            Collection<?> collection = (Collection<?>) source;
            Collection<Object> target = createCollectionFor(collection);

            for (Object columns : collection) {
                target.add(type.isInstance(columns) ? columns : converter.convert(columns));
            }

            return (T) target;
        }

        if (source instanceof Stream && method.isStreamQuery()) {
            return (T) ((Stream<Object>) source).map(t -> type.isInstance(t) ? t : converter.convert(t));
        }

        if (ReactiveWrapperConverters.supports(source.getClass())) {
            return (T) ReactiveWrapperConverters.map(source, converter::convert);
        }

        return (T) converter.convert(source);
    }

//    private ResultProcessor withType(Class<?> type) {
//
//        AppProjectionReturnedType returnedType = new AppProjectionReturnedType(type, domainClass, factory);
//        return new ResultProcessor(method, converter.withType(returnedType), factory, returnedType, domainClass);
//    }

    /**
     * Creates a new {@link Collection} for the given source. Will try to create an instance of the source collection's
     * type first falling back to creating an approximate collection if the former fails.
     *
     * @param source must not be {@literal null}.
     * @return
     */
    private static Collection<Object> createCollectionFor(Collection<?> source) {

        try {
            return CollectionFactory.createCollection(source.getClass(), source.size());
        } catch (RuntimeException o_O) {
            return CollectionFactory.createApproximateCollection(source, source.size());
        }
    }

    //    @RequiredArgsConstructor(staticName = "of")
    private static class ChainingConverter implements Converter<Object, Object> {

        private final @NonNull
        Class<?> targetType;

        private final @NonNull
        Converter<Object, Object> delegate;

        private ChainingConverter(Class<?> targetType, Converter<Object, Object> delegate) {
            this.targetType = targetType;
            this.delegate = delegate;
        }

        static ChainingConverter of(Class<?> targetType, Converter<Object, Object> delegate) {
            return new ChainingConverter(targetType, delegate);
        }

        /**
         * Returns a new {@link ResultProcessor.ChainingConverter} that hands the elements resulting from the current conversion to the
         * given {@link Converter}.
         *
         * @param converter must not be {@literal null}.
         * @return
         */
        public ResultProcessor.ChainingConverter and(final Converter<Object, Object> converter) {

            Assert.notNull(converter, "Converter must not be null!");

            return new ResultProcessor.ChainingConverter(targetType, source -> {

                Object intermediate = ResultProcessor.ChainingConverter.this.convert(source);

                return intermediate == null || targetType.isInstance(intermediate) ? intermediate
                        : converter.convert(intermediate);
            });
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
         */
        @Nullable
        @Override
        public Object convert(Object source) {
            return delegate.convert(source);
        }
    }

    /**
     * A simple {@link Converter} that will return the source value as is.
     *
     * @author Oliver Gierke
     * @since 1.12
     */
    private static enum NoOpConverter implements Converter<Object, Object> {

        INSTANCE;

        /*
         * (non-Javadoc)
         * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
         */
//        @Nonnull
        @Override
        public Object convert(Object source) {
            return source;
        }
    }

    private static class ProjectingConverter implements Converter<Object, Object> {

        private final @NonNull
        AppProjectionReturnedType type;
        private final @NonNull
        ProjectionFactory factory;
        private final @NonNull
        ConversionService conversionService;

        ProjectingConverter(AppProjectionReturnedType type, ProjectionFactory factory, ConversionService conversionService) {
            this.type = type;
            this.factory = factory;
            this.conversionService = conversionService;
        }

        /**
         * Creates a new {@link ResultProcessor.ProjectingConverter} for the given {@link ReturnedType} and {@link ProjectionFactory}.
         *
         * @param type    must not be {@literal null}.
         * @param factory must not be {@literal null}.
         */
        ProjectingConverter(AppProjectionReturnedType type, ProjectionFactory factory) {
            this(type, factory, DefaultConversionService.getSharedInstance());
        }

        /**
         * Creates a new {@link ResultProcessor.ProjectingConverter} for the given {@link ReturnedType}.
         *
         * @param type must not be {@literal null}.
         * @return
         */
        ProjectingConverter withType(AppProjectionReturnedType type) {

            Assert.notNull(type, "ReturnedType must not be null!");

            return new ResultProcessor.ProjectingConverter(type, factory, conversionService);
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
         */
        @Nullable
        @Override
        public Object convert(Object source) {

            Class<?> targetType = type.getReturnedType();

            if (targetType.isInterface()) {
                return factory.createProjection(targetType, getProjectionTarget(source));
            }

            return conversionService.convert(source, targetType);
        }

        private Object getProjectionTarget(Object source) {

            if (source != null && source.getClass().isArray()) {
                source = Arrays.asList((Object[]) source);
            }

            if (source instanceof Collection) {
                return toMap((Collection<?>) source, type.getInputPropertiesAliases());
            }

            return source;
        }

        private static Map<String, Object> toMap(Collection<?> values, List<String> names) {

            int i = 0;
            Map<String, Object> result = new HashMap<>(values.size());

            for (Object element : values) {
                result.put(names.get(i++), element);
            }

            return result;
        }
    }
}

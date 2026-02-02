package com.scnsoft.eldermark.dao.basic;

import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.util.Lazy;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Our custom light-weighted version of {@link org.springframework.data.jpa.repository.query.JpaQueryMethod}
 * to be used with {@link com.scnsoft.eldermark.dao.basic.ResultProcessor}
 */
public class AppProjectingQueryMethod extends QueryMethod {

    private final Lazy<Boolean> isCollectionQuery;

    private static final Set<Class<?>> NATIVE_ARRAY_TYPES;

    static {

        Set<Class<?>> types = new HashSet<>();
        types.add(byte[].class);
        types.add(Byte[].class);
        types.add(char[].class);
        types.add(Character[].class);

        NATIVE_ARRAY_TYPES = Collections.unmodifiableSet(types);
    }

    /**
     * Creates a new {@link QueryMethod} from the given parameters. Looks up the correct query to use for following
     * invocations of the method given.
     *
     * @param method   must not be {@literal null}.
     * @param metadata must not be {@literal null}.
     * @param factory  must not be {@literal null}.
     */
    public AppProjectingQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
        super(method, metadata, factory);

        this.isCollectionQuery = Lazy
                .of(() -> super.isCollectionQuery() && !NATIVE_ARRAY_TYPES.contains(method.getReturnType()));
    }

    @Override
    public boolean isCollectionQuery() {
        return this.isCollectionQuery.get();
    }
}

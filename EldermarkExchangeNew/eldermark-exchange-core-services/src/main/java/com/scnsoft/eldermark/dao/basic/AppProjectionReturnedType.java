package com.scnsoft.eldermark.dao.basic;

import com.scnsoft.eldermark.util.StreamUtils;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Inspired by Spring's {@link org.springframework.data.repository.query.ReturnedType} and
 * {@link org.springframework.data.repository.query.ReturnedType.ReturnedInterface}
 */
public class AppProjectionReturnedType {

    private static final String UNSUPPORTED_PROJECTION_TYPE = "Only interface projections are currently supported!";

    private final ProjectionFactory projectionFactory;
    private final Class<?> returnedType;
    private final Class<?> domainType;
    private final List<InputProperty> inputProperties;
    private final List<InputProperty> evaluatedProperties;

    public AppProjectionReturnedType(Class<?> returnedType, Class<?> domainType, ProjectionFactory projectionFactory) {
        Assert.notNull(projectionFactory, "Projection Factory must not be null");
        Assert.notNull(domainType, "Domain Type must not be null");
        Assert.notNull(returnedType, "Returned Type must not be null");
        Assert.state(returnedType.isInterface(), UNSUPPORTED_PROJECTION_TYPE);

        this.projectionFactory = projectionFactory;
        this.returnedType = returnedType;
        this.domainType = domainType;

        this.inputProperties = getInputProperties();
        this.evaluatedProperties = getEvaluatedProperties();
    }

    public List<InputProperty> getInputProperties() {
        if (this.inputProperties != null) {
            return new ArrayList<>(this.inputProperties);
        }

        var information = (AppProjectionFactory.AppProjectionInformation) projectionFactory.getProjectionInformation(returnedType);
        return fetchProperties(information);
    }

    public List<InputProperty> getEvaluatedProperties() {
        if (this.evaluatedProperties != null) {
            return new ArrayList<>(this.evaluatedProperties);
        }
        return getInputProperties().stream()
                .filter(inputProperty -> inputProperty.getEvaluatedPropertyProcessor() != null)
                .collect(Collectors.toList());
    }


    public List<String> getInputPropertiesAliases() {
        return getInputProperties().stream().map(InputProperty::getAlias).collect(Collectors.toList());
    }

    private List<InputProperty> fetchProperties(AppProjectionFactory.AppProjectionInformation information) {
        return information.getInputProperties()
                .stream()
                .map(prop -> createInputProperty(prop, information))
                .filter(StreamUtils.distinctByKey(InputProperty::getAlias))
                .collect(Collectors.toList());
    }

    private InputProperty createInputProperty(PropertyDescriptor prop, AppProjectionFactory.AppProjectionInformation information) {
        var evaluatedPropertyProcessor = information.getEvaluatedPropertyProcessor(prop.getName());
        PropertyPath propertyPath = null;
        if (evaluatedPropertyProcessor == null) {
            propertyPath = PropertyPath.from(prop.getName(), domainType);
        }
        return new InputProperty(evaluatedPropertyProcessor, propertyPath, prop.getName());
    }

    public final boolean isInstance(@Nullable Object source) {
        return getReturnedType().isInstance(source);
    }

    protected Class<?> getReturnedType() {
        return returnedType;
    }

    public boolean isProjecting() {
        //event if domainType implements returnedType we still want tuple to be converted to proxy
        return !domainType.equals(returnedType);
    }

    //-------------- Below code snippets can be used for future projections enhancements ------

/*
    private static final Set<Class<?>> VOID_TYPES = new HashSet<>(Arrays.asList(Void.class, void.class));

    private List<PropertyPath> fetchProperties(ProjectionInformation information, PropertyPath base) {
        return information.getInputProperties()
                .stream()
                .map(prop -> fetchProperties(prop, base))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<PropertyPath> fetchProperties(PropertyDescriptor descriptor, PropertyPath base) {

        if (isUnwrapNeeded(descriptor)) {
            return fetchProperties(descriptor.getReadMethod(), append(base, descriptor.getName()));
        }
        return Collections.singletonList(append(base, descriptor.getName()));
    }

    private PropertyPath append(PropertyPath base, String name) {
        if (base == null) {
            return PropertyPath.from(name, domainType);
        }
        return base.nested(name);
    }

    private boolean isUnwrapNeeded(PropertyDescriptor descriptor) {
        var type = descriptor.getPropertyType();
        return type.isInterface() || type.isAssignableFrom(Collection.class);
    }

    private List<PropertyPath> fetchProperties(Method projectionReadMethod, PropertyPath base) {
        //todo think how to avoid cyclic dependencies

        var projectionType = projectionReadMethod.getReturnType();

        if (projectionType.isInterface()) {
            if (Collection.class.isAssignableFrom(projectionType)) {
                System.out.println("collection sub-projection");

                //switch to Collection's generic parameter
                var retType = (ParameterizedType) projectionReadMethod.getGenericReturnType();
                projectionType = (Class<?>) retType.getActualTypeArguments()[0];

                //Spring way (to be tested)
                projectionType = ClassTypeInformation.fromReturnTypeOf(projectionReadMethod).getComponentType().getType()

                if (!projectionType.isInterface() && !isElementCollectionSupported(projectionType)) {
                    throw new RuntimeException(UNSUPPORTED_PROJECTION_TYPE);
                }
            }

            System.out.println("interface sub-projection");
            var information = projectionFactory.getProjectionInformation(projectionType);
            return fetchProperties(information, base);
        }
        return Collections.emptyList();
    }

    private boolean isElementCollectionSupported(Class<?> type) {
        //todo implement
//        Defines a collection of instances of a basic type or embeddable class.
// Must be specified if the collection is to be mapped by means of a collection table.
        return false;
    }

    private boolean isDto(Class<?> type) {
        return !Object.class.equals(type) && //
                !type.isEnum() && //
                !isDomainSubtype(type) && //
                !isPrimitiveOrWrapper(type) && //
                !Number.class.isAssignableFrom(type) && //
                !VOID_TYPES.contains(type) && //
                !type.getPackage().getName().startsWith("java.");
    }

    private boolean isDomainSubtype(Class<?> type) {
        return domainType.equals(type) && domainType.isAssignableFrom(type);
    }

    private boolean isPrimitiveOrWrapper(Class<?> type) {
        return ClassUtils.isPrimitiveOrWrapper(type);
    }
    */
}

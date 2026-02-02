package com.scnsoft.eldermark.dao.basic;

import com.scnsoft.eldermark.dao.basic.evaluated.EvaluatedProperty;
import com.scnsoft.eldermark.dao.basic.evaluated.factory.EvaluatedPropertyProcessorFactory;
import com.scnsoft.eldermark.dao.basic.evaluated.processor.EvaluatedPropertyProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.jpa.projection.CollectionAwareProjectionFactory;
import org.springframework.data.projection.ProjectionInformation;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The goal of this factory is to provide entry point for further projections customizations
 * <p>
 * <p>
 * If there is a need to handle collection properties ourselves, then get rid of CollectionAwareProxyProjectionFactory
 * from hierarchy and extend SpelAwareProxyProjectionFactory directly
 */
public class AppProjectionFactory extends CollectionAwareProjectionFactory {

    private BeanFactory beanFactory;

    @Override
    protected ProjectionInformation createProjectionInformation(Class<?> projectionType) {
        return new AppProjectionInformation(projectionType, getEvaluatedPropertyProcessorFactory());
    }

    private EvaluatedPropertyProcessorFactory getEvaluatedPropertyProcessorFactory() {
        return beanFactory.getBean(EvaluatedPropertyProcessorFactory.class);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        super.setBeanFactory(beanFactory);
        this.beanFactory = beanFactory;
    }

    /**
     * [copypaste] from CollectionAwareProjectionFactory$CollectionAwareProjectionInformation
     * <p>
     * Reusing Spring's private projectionInformation
     */
    private static class CollectionAwareProjectionInformation extends SpelAwareProjectionInformation {

        CollectionAwareProjectionInformation(Class<?> projectionType) {
            super(projectionType);
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.projection.SpelAwareProxyProjectionFactory.SpelAwareProjectionInformation#isInputProperty(java.beans.PropertyDescriptor)
         */
        @Override
        protected boolean isInputProperty(PropertyDescriptor descriptor) {

            if (!super.isInputProperty(descriptor)) {
                return false;
            }

            return !(Collection.class.isAssignableFrom(descriptor.getPropertyType()) //
                    || Map.class.isAssignableFrom(descriptor.getPropertyType()));
        }
    }

    protected static class AppProjectionInformation extends CollectionAwareProjectionInformation {

        private Map<String, EvaluatedPropertyProcessor> evaluatedProperties = new HashMap<>();

        protected AppProjectionInformation(Class<?> projectionType,
                                           EvaluatedPropertyProcessorFactory evaluatedPropertyProcessorFactory) {
            super(projectionType);
            getInputProperties()
                    .forEach(prop -> {
                        var method = prop.getReadMethod();

                        if (method == null) {
                            return;
                        }

                        var annotation = AnnotationUtils.getAnnotation(method, EvaluatedProperty.class);
                        if (annotation == null) {
                            return;
                        }

                        evaluatedProperties.put(
                                prop.getName(),
                                evaluatedPropertyProcessorFactory.createEvaluatedPropertyProcessor(annotation)
                        );
                    });
        }

        public EvaluatedPropertyProcessor getEvaluatedPropertyProcessor(String propName) {
            return evaluatedProperties.getOrDefault(propName, null);
        }

        //below code snippets can be used for further projections modifications
        /*
        @Override
        protected boolean isInputProperty(PropertyDescriptor descriptor) {
            validatePropertySupported(descriptor);

            return super.isInputProperty(descriptor);
        }

        private void validatePropertySupported(PropertyDescriptor descriptor) {
            if (isSpELProperty(descriptor)) {
                throw new IllegalArgumentException("SpEL properties with @Value annotation are not supported in custom projections: "
                        + descriptor.getReadMethod().getName());
            }
        }

        private boolean isSpELProperty(PropertyDescriptor descriptor) {
            Method readMethod = descriptor.getReadMethod();

            if (readMethod == null) {
                return false;
            }

            return AnnotationUtils.findAnnotation(readMethod, Value.class) != null;

        }
        */
    }
}

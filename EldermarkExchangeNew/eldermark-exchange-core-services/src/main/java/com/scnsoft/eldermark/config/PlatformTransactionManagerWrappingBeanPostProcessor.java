package com.scnsoft.eldermark.config;

import com.scnsoft.eldermark.jpa.DelegatingPlatformTransactionManager;
import com.scnsoft.eldermark.jpa.TransactionListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

public class PlatformTransactionManagerWrappingBeanPostProcessor implements BeanPostProcessor {

    private final List<TransactionListener> listeners;

    public PlatformTransactionManagerWrappingBeanPostProcessor(List<TransactionListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof PlatformTransactionManager) {
            return new DelegatingPlatformTransactionManager((PlatformTransactionManager) bean, listeners);
        }
        return bean;
    }
}

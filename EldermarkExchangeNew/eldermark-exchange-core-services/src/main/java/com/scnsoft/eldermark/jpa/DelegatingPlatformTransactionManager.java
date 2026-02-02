package com.scnsoft.eldermark.jpa;

import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;

import java.util.List;


public class DelegatingPlatformTransactionManager implements PlatformTransactionManager,
        ResourceTransactionManager, BeanFactoryAware, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(DelegatingPlatformTransactionManager.class);

    private final PlatformTransactionManager delegate;
    private final List<TransactionListener> listeners;

    public DelegatingPlatformTransactionManager(PlatformTransactionManager delegate,
                                                List<TransactionListener> listeners) {
        this.delegate = delegate;
        this.listeners = listeners;

        this.listeners.stream()
                .filter(listener -> listener instanceof PlatformTransactionManagerAware)
                .forEach(l -> ((PlatformTransactionManagerAware) l).setPlatformTransactionManager(this));
    }

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        var txStatus = delegate.getTransaction(definition);
        logger.debug("DelegatingPlatformTransactionManager.getTransaction from delegate {}, definition {}", txStatus, definition);
        if (txStatus.isNewTransaction()) {
            for (var listener : listeners) {
                listener.afterTransactionBegin(txStatus);
            }
        }
        return txStatus;
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {
        delegate.commit(status);
    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {
        delegate.rollback(status);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (delegate instanceof BeanFactoryAware) {
            ((BeanFactoryAware) delegate).setBeanFactory(beanFactory);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (delegate instanceof InitializingBean) {
            ((InitializingBean) delegate).afterPropertiesSet();
        }
    }

    @Override
    public Object getResourceFactory() {
        Assert.state(delegate instanceof ResourceTransactionManager);
        return ((ResourceTransactionManager) delegate).getResourceFactory();
    }
}

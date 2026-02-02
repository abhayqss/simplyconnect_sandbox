package com.scnsoft.eldermark.dao.ccd;

import com.scnsoft.eldermark.dao.ResidentDaoImpl;
import com.scnsoft.eldermark.shared.ccd.CcdSectionDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class CcdSectionDaoFactory implements ApplicationContextAware {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ResidentDaoImpl residentDao;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public <T extends CcdSectionDto> GenericCcdSectionDaoImpl<T> getCcdSectionDao(Class<T> clazz) {
        return (GenericCcdSectionDaoImpl<T>) getDaoBean(clazz);
    }

    protected <T extends CcdSectionDto> Object getDaoBean(Class<T> clazz) {
        Object bean;

        String beanName = getDaoBeanNameFromClass(clazz);
        if (applicationContext.containsBean(beanName)) {
            bean = applicationContext.getBean(beanName);
        } else {
            GenericCcdSectionDaoImpl beanInstance = new GenericCcdSectionDaoImpl<>(clazz, entityManager, residentDao);
            bean = applicationContext.getAutowireCapableBeanFactory().initializeBean(beanInstance, beanName);
        }

        return bean;
    }

    protected String getDaoBeanNameFromClass(Class clazz) {
        String className = StringUtils.uncapitalize(clazz.getSimpleName());
        return className + "Dao";
    }
}

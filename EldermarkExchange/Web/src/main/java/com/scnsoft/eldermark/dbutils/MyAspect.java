package com.scnsoft.eldermark.dbutils;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

/**
 * Created by averazub on 10/31/2016.
 */
//TODO better do Certificate handling through aspects
/*@Component
@Aspect*/
public class MyAspect {
    public MyAspect() {
        System.out.println("Created");
    }

    @Pointcut("execution (* javax.persistence.EntityManager.close(..))")
    public void executionPointCut2() {
        System.out.println("Hello4");
    }

    @AfterReturning(pointcut = "executionPointCut2()")
    public void execution2() {
        System.out.println("HelloGreat");
    }

    @Pointcut("execution (* javax.persistence.EntityManagerFactory.createEntityManager(..))")
    public void executionPointCut() {
        System.out.println("Hello3");
    }

    @AfterReturning(pointcut = "executionPointCut()", returning = "entityManager")
    public void execution(EntityManager entityManager) {
        System.out.println("Hello");
    }



    @Pointcut("execution (* org.hibernate.Session.close())")
    public void sessionPointcut() {
        System.out.println("Hello3");
    }

    @AfterReturning(pointcut = "sessionPointcut()")
    public void executionSession() {
        System.out.println("Hello100500");
    }


}

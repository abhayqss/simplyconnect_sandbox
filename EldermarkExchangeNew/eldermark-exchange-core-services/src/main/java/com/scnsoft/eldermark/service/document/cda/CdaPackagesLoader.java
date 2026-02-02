package com.scnsoft.eldermark.service.document.cda;

import com.scnsoft.eldermark.util.cda.CustomCDAUtil;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.EPackage.Registry;

import org.eclipse.mdht.uml.cda.util.CDAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Used to load CDA jar library files into memory
 * {@link org.eclipse.mdht.uml.cda.util.CDAUtil} cannot be used since pom jar files are not present in class path of
 * Spring Boot application which is running as standalone jar file. Instead, Spring Boot's 'fat' jar bundles all the
 * pom dependencies in BOOT-INF/lib/ directory inside jar.
 * <p>
 * CDAUtil assumes that the jar are in class path.
 *
 * @see CDAUtil
 */
@Component

public class CdaPackagesLoader implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(CdaPackagesLoader.class);

    /**
     * Internally, loaded packages are stored inside {@link Registry.INSTANCE},
     * which is created by {@link EPackageRegistryImpl#createGlobalRegistry()}
     * In current application instance of {@link EPackageRegistryImpl.Delegator} is created.
     *
     * The issue is that {@link EPackageRegistryImpl.Delegator} maps loaded packages to current context classloader.
     * During initialization application has context classloader different to context classloader for api calls to
     * @Controller methods. As a result for api calls {@link Registry.INSTANCE} is empty, because packages are loaded
     * for another context classloader.
     *
     * In order to get around this issue we set system property as below to specify, that instead
     * of {@link EPackageRegistryImpl.Delegator} instance of {@link EPackageRegistryImpl} should be created.
     * It simply stores loaded packages, so all the loaded packages available in case of different context classloaders.
     *
     * P.S. We could initialize packages during api calls so that classloaders are the same, but we want
     * to leave the ability to parse CCDs in @Scheduled methods, which has the same context classloader as
     * during application initialization.
     *
     * @see {@link EPackageRegistryImpl#createGlobalRegistry()}
     */
    static {
        System.setProperty("org.eclipse.emf.ecore.EPackage.Registry.INSTANCE", "org.eclipse.emf.ecore.impl.EPackageRegistryImpl");
    }


    /**
     * Loads jar dependencies as resources from BOOT-INF/lib/ and passes them to {@link CustomCDAUtil} to be loaded.
     * @param contextRefreshedEvent
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        var ctx = contextRefreshedEvent.getApplicationContext();
        try {
            var dependencies = ctx.getResources("classpath:BOOT-INF/lib/*");
            CustomCDAUtil.loadPackages(dependencies);
        } catch (IOException e) {
            //typically it occurs in development move when developer runs Spring application directly.
            //In this case there is no 'fat' jar file, instead all the dependencies are in classpath and can be loaded
            //normally by CDAUtil.loadPackages()
            logger.info("Failed to load dependencies from BOOT-INF/lib/ (application is run directly by developer?), " +
                    "loading cda dependencies from classpath...");
            CDAUtil.loadPackages();
        }
    }
}

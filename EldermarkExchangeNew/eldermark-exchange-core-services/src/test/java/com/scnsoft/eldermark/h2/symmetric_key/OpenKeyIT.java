package com.scnsoft.eldermark.h2.symmetric_key;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.h2.BaseH2IT;
import com.scnsoft.eldermark.h2.OpenKeySessionTestSupport;
import com.scnsoft.eldermark.h2.TestApplicationH2Config;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class OpenKeyIT extends BaseH2IT {

    //@Transactional on test classes are actually handled differently from ones on application beans
    //(see https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#testcontext-tx)
    //therefore to perform clean testing we need to register additional beans with desired transaction settings
    @TestConfiguration
    public static class OpenKeyITConfig {

        @Bean
        public OpenKeyITConsumer transactionalOpenKeyITAction() {
            return new OpenKeyITConsumer() {

                @Override
                @Transactional
                public void accept(Runnable action) {
                    action.run();
                }
            };
        }

        @Bean
        public OpenKeyITConsumer transactionalNewOpenKeyITAction() {
            return new OpenKeyITConsumer() {

                @Override
                @Transactional(propagation = Propagation.REQUIRES_NEW)
                public void accept(Runnable action) {
                    action.run();
                }
            };
        }
    }

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    @Qualifier("transactionalOpenKeyITAction")
    private OpenKeyITConsumer transactionalOpenKeyITAction;

    @Autowired
    @Qualifier("transactionalNewOpenKeyITAction")
    private OpenKeyITConsumer transactionalNewOpenKeyITAction;

    @Autowired
    private TestApplicationH2Config.PhysicalSessionIdFetcher physicalSessionIdFetcher;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @BeforeEach()
    public void disallowOpenSameSession() {
        OpenKeySessionTestSupport.setAllowOpenSameSession(false);
    }

    @Test
    public void test_whenEntersTransactionalMethod_keyOpenedOnce() {
        var sessions = new ArrayList<Integer>();

        transactionalOpenKeyITAction.accept(() -> {
            sessions.add(physicalSessionIdFetcher.get());
        });

        assertAllOpened(sessions);
    }

    @Test
    public void test_whenCallsRepositoryInNonTransactional_keyOpenedForEachCall() {
        var numberOfInvocations = 3;

        for (int i = 0; i < numberOfInvocations; ++i) {
            OpenKeySessionTestSupport.reset();
            organizationDao.findById(1L);

            Assertions.assertThat(OpenKeySessionTestSupport.get())
                    .hasSize(1);
        }
    }

    @Test
    public void test_whenEntersTransactionalInTransactional_keyOpenedOnce() {
        var sessions = new ArrayList<Integer>();

        transactionalOpenKeyITAction.accept(
                () -> {
                    sessions.add(physicalSessionIdFetcher.get());
                    transactionalOpenKeyITAction.accept(
                            () -> {
                                sessions.add(physicalSessionIdFetcher.get());
                            }
                    );
                });

        assertAllOpened(sessions);
    }

    @Test
    public void test_whenCallsRepositoryInTransactional_keyOpenedOnce() {
        var sessions = new ArrayList<Integer>();

        transactionalOpenKeyITAction.accept(
                () -> {
                    organizationDao.findById(1L);
                    sessions.add(physicalSessionIdFetcher.get());
                }
        );

        assertAllOpened(sessions);
    }

    @Test
    public void test_whenEntersTransactionalNewInTransactional_keyOpenedTwice() {
        var sessions = new ArrayList<Integer>();

        transactionalOpenKeyITAction.accept(
                () -> {
                    sessions.add(physicalSessionIdFetcher.get());
                    transactionalNewOpenKeyITAction.accept(() -> {
                        sessions.add(physicalSessionIdFetcher.get());
                    });
                }
        );

        assertAllOpened(sessions);
    }


    @Test
    public void test_whenCallsRepositoryProjectionSpecificationInTransactional_keyOpenedOnce() {
        var sessions = new ArrayList<Integer>();

        transactionalOpenKeyITAction.accept(
                () -> {
                    organizationDao.findById(1L, IdAware.class);
                    sessions.add(physicalSessionIdFetcher.get());
                }
        );

        assertAllOpened(sessions);
    }


    @Test
    public void test_whenCallsRepositoryProjectionSpecificationInNonTransactional_keyOpenedForEachCall() {
        var numberOfInvocations = 3;

        for (int i = 0; i < numberOfInvocations; ++i) {
            OpenKeySessionTestSupport.reset();
            organizationDao.findById(1L, IdAware.class);

            Assertions.assertThat(OpenKeySessionTestSupport.get())
                    .isNotEmpty();
        }
    }


    @Test
    @Disabled("Key not opened because query methods are non Transactional by default")
    public void test_whenCallsRepositoryNonTxQueryMethodInNonTransactional_keyOpenedForEachCall() {
        var numberOfInvocations = 3;

        for (int i = 0; i < numberOfInvocations; ++i) {
            OpenKeySessionTestSupport.reset();
            organizationDao.existsByOid("oid");

            Assertions.assertThat(OpenKeySessionTestSupport.get())
                    .isNotEmpty();
        }
    }

    @Test
    public void test_whenEntersProgrammaticTransaction_keyOpenedOnce() {
        var sessions = new ArrayList<Integer>();

        var txTemplate = new TransactionTemplate(platformTransactionManager);

        txTemplate.executeWithoutResult(x -> {
            sessions.add(physicalSessionIdFetcher.get());
        });

        assertAllOpened(sessions);
    }

    @Test
    public void test_whenEntersTransactionalInProgrammaticTransaction_keyOpenedOnce() {
        var sessions = new ArrayList<Integer>();

        var txTemplate = new TransactionTemplate(platformTransactionManager);

        txTemplate.executeWithoutResult(x -> {
            sessions.add(physicalSessionIdFetcher.get());
            transactionalOpenKeyITAction.accept(
                    () -> {
                        sessions.add(physicalSessionIdFetcher.get());
                    }
            );
        });

        assertAllOpened(sessions);
    }

    @Test
    public void test_whenEntersTransactionalNewInProgrammaticTransaction_keyOpenedTwice() {
        var sessions = new ArrayList<Integer>();

        var txTemplate = new TransactionTemplate(platformTransactionManager);

        txTemplate.executeWithoutResult(x -> {
            sessions.add(physicalSessionIdFetcher.get());
            transactionalNewOpenKeyITAction.accept(
                    () -> {
                        sessions.add(physicalSessionIdFetcher.get());
                    }
            );
        });

        assertAllOpened(sessions);
    }

    @Test
    public void test_whenEntersProgrammaticTransactionNewInProgrammaticTransaction_keyOpenedTwice() {
        var sessions = new ArrayList<Integer>();

        var txTemplate = new TransactionTemplate(platformTransactionManager);

        var txNewTemplate = new TransactionTemplate(platformTransactionManager);
        txNewTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);


        txTemplate.executeWithoutResult(x -> {
            sessions.add(physicalSessionIdFetcher.get());
            txNewTemplate.executeWithoutResult(xx -> {
                sessions.add(physicalSessionIdFetcher.get());
            });
        });

        assertAllOpened(sessions);
    }

    @Test
    public void test_whenCallsRepositoryInProgrammaticTransaction_keyOpenedOnce() {
        var sessions = new ArrayList<Integer>();
        var txTemplate = new TransactionTemplate(platformTransactionManager);

        txTemplate.executeWithoutResult(
                x -> {
                    organizationDao.findById(1L);
                    sessions.add(physicalSessionIdFetcher.get());
                }
        );

        assertAllOpened(sessions);
    }

    private void assertAllOpened(Collection<Integer> actual) {
        Assertions.assertThat(actual)
                .containsOnly(
                        OpenKeySessionTestSupport.get().toArray(new Integer[0])
                );
    }

    public interface OpenKeyITConsumer extends Consumer<Runnable> {
    }
}

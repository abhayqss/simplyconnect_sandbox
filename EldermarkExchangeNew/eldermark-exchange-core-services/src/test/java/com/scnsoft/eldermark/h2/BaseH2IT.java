package com.scnsoft.eldermark.h2;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@SpringBootTest(classes = {TestApplicationH2Config.class})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class
})
public abstract class BaseH2IT {

    @BeforeEach
    public void resetOpenTransactions() {
        OpenKeySessionTestSupport.reset();
        OpenKeySessionTestSupport.setAllowOpenSameSession(true);
    }
}

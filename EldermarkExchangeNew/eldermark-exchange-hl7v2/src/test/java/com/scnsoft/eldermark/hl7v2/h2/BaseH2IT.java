package com.scnsoft.eldermark.hl7v2.h2;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@SpringBootTest(classes = {TestApplicationH2Config.class})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class
})
@ActiveProfiles("h2") //need to have it on test class instead of config class to override 'local' profile
public abstract class BaseH2IT {

}

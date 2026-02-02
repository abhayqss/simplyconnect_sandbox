package com.scnsoft.eldermark.integration;

import io.github.robwin.swagger.test.SwaggerAssertions;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

/**
 * For Consumer Driven Contract tests, Assertj-Swagger fails the test if it finds missing resources, methods, models,
 * or properties in the implementation which are required by the consumer specification.
 *
 * @author phomal
 * Created on 5/18/2017.
 */
@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AssertJSwaggerConsumerDrivenTest {

    @LocalServerPort
    int randomPort;

    @Test
    public void validateThatImplementationSatisfiesConsumerSpecification() {
        File designFirstSwagger = new File(AssertJSwaggerConsumerDrivenTest.class.getResource("/api/swagger_m2.yaml").getFile());
        SwaggerAssertions.assertThat("http://localhost:" + randomPort + "/v2/api-docs")
                .satisfiesContract(designFirstSwagger.getAbsolutePath());
    }

}

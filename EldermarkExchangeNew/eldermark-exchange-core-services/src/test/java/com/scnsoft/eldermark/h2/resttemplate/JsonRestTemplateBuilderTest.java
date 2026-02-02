package com.scnsoft.eldermark.h2.resttemplate;

import com.scnsoft.eldermark.h2.BaseH2IT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestGatewaySupport;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class JsonRestTemplateBuilderTest extends BaseH2IT {

    @Autowired
    @Qualifier("jsonRestTemplateBuilder")
    private RestTemplateBuilder restTemplateBuilder;

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setUp() {
        RestGatewaySupport gateway = new RestGatewaySupport();
        restTemplate = restTemplateBuilder.build();
        gateway.setRestTemplate(restTemplate);
        mockServer = MockRestServiceServer.createServer(gateway);
    }

    @Test
    void testPostBodyIsJsonByDefaultAndParsesDates() {
        var url = "https://test.com";

        mockServer.expect(ExpectedCount.once(),
                        requestTo(url))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string("{\"field1\":\"test\",\"field2\":42}"))
                .andRespond(withSuccess("", APPLICATION_JSON)
                        .body("{\"localDate\":\"2020-01-30\",\"instant\":\"" + Instant.now() + "\"}")
                );

        var requestBody = new Data();
        requestBody.setField1("test");
        requestBody.setField2(42);
        var response = restTemplate.postForEntity(url, requestBody, Response.class);

        assertThat(response.getBody().getLocalDate()).isEqualTo(LocalDate.of(2020, 1, 30));
    }


    static class Data {
        private String field1;
        private Integer field2;

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public Integer getField2() {
            return field2;
        }

        public void setField2(Integer field2) {
            this.field2 = field2;
        }
    }

    public static class Response {
        //        @JsonDeserialize(using = LocalDateDeserializer.class)
        private LocalDate localDate;
        private Instant instant;

        public LocalDate getLocalDate() {
            return localDate;
        }

        public void setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
        }

        public Instant getInstant() {
            return instant;
        }

        public void setInstant(Instant instant) {
            this.instant = instant;
        }
    }

}

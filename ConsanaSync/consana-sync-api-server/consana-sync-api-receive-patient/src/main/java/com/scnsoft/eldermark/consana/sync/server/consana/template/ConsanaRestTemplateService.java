package com.scnsoft.eldermark.consana.sync.server.consana.template;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

public interface ConsanaRestTemplateService {

    RestTemplate getConsanaRestTemplate();

    HttpHeaders createConsanaHttpHeaders(String xclOrganizationId);
}

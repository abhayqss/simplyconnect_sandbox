package com.scnsoft.eldermark.consana.sync.client.services.senders.template;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

public interface ConsanaRestTemplateService {

    RestTemplate getConsanaRestTemplate();

    HttpHeaders createConsanaHttpHeaders();
}

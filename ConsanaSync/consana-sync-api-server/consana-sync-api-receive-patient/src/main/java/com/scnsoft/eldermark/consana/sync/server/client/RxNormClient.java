package com.scnsoft.eldermark.consana.sync.server.client;

import com.scnsoft.eldermark.consana.sync.server.client.dto.RxNormResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "rxnorm", fallback = FallbackRxNormClient.class, url = "${feign.rxnorm.baseurl}")
public interface RxNormClient {

    @RequestMapping(method = RequestMethod.GET, value = "rxcui/{rxcui}/allrelated", produces = "application/json")
    RxNormResponseDto getRxNorm(@PathVariable("rxcui") String rxcui);

}

package com.scnsoft.eldermark.consana.sync.server.client;

import com.scnsoft.eldermark.consana.sync.server.client.dto.RxNormResponseDto;

public class FallbackRxNormClient implements RxNormClient {

    @Override
    public RxNormResponseDto getRxNorm(String rxcui) {
        return null;
    }
}

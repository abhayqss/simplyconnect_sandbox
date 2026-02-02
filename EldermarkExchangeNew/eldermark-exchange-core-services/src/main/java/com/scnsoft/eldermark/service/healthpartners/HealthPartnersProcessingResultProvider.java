package com.scnsoft.eldermark.service.healthpartners;

import java.util.concurrent.CompletableFuture;

public interface HealthPartnersProcessingResultProvider {

    CompletableFuture<Long> getFileLogFuture(String fileName);
}

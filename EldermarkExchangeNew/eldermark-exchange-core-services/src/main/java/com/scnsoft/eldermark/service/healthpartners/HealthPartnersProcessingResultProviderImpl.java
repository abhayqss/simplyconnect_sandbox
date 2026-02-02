package com.scnsoft.eldermark.service.healthpartners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@Service
public class HealthPartnersProcessingResultProviderImpl implements HealthPartnersProcessingResultProvider {
    private static final Logger logger = LoggerFactory.getLogger(HealthPartnersProcessingResultProviderImpl.class);

    private final Map<String, List<CompletableFuture<Long>>> waitingFuturesMap =
            new ConcurrentHashMap<>();


    @Override
    public CompletableFuture<Long> getFileLogFuture(String fileName) {
        var result = new CompletableFuture<Long>();
        var queue = waitingFuturesMap.computeIfAbsent(fileName, x -> new CopyOnWriteArrayList<>());
        queue.add(result);

        //todo remove completablefuture from map after some timeout?
        return result;
    }

    @EventListener
    public void handleFileProcessedEvent(HealthPartnersFileProcessedEvent event) {
        logger.info("Received event {}", event);

        var waitingFutures = waitingFuturesMap.getOrDefault(event.getFileName(), Collections.emptyList());
        waitingFuturesMap.remove(event.getFileName());

        for (var future : waitingFutures) {
            future.complete(event.getHpFileLogId());
        }
    }
}

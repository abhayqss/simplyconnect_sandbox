package com.scnsoft.eldermark.services.inbound;

import com.scnsoft.eldermark.entity.inbound.summary.ProcessingSummary;

import java.util.List;

public interface InboundFileGateway<T, P extends ProcessingSummary> {

    List<T> loadFiles();

    void afterProcessingStatusOk(T remoteFile, P summary);

    void afterProcessingStatusWarn(T remoteFile, P summary);

    void afterProcessingStatusError(T remoteFile, P summary);
}

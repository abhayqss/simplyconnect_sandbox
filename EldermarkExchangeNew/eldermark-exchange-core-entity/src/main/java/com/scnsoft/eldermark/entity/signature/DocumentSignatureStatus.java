package com.scnsoft.eldermark.entity.signature;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum DocumentSignatureStatus {
    SIGNED("Signed"),
    RECEIVED("Received"),
    REQUESTED("Signature requested"),
    SENT("Sent"),
    REQUEST_EXPIRED("Signature request expired"),
    FAILED("Signature failed"),
    REQUEST_CANCELED("Canceled");

    private final String title;

    DocumentSignatureStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    private static final Map<DocumentSignatureStatus, List<DocumentSignatureRequestStatus>> statusToRequestStatusMap =
            Map.of(
                    SIGNED, List.of(DocumentSignatureRequestStatus.SIGNED),
                    RECEIVED, List.of(DocumentSignatureRequestStatus.REVIEWED),
                    REQUESTED, List.of(DocumentSignatureRequestStatus.SIGNATURE_REQUESTED),
                    SENT, List.of(DocumentSignatureRequestStatus.REVIEW_REQUESTED),
                    FAILED, List.of(
                            DocumentSignatureRequestStatus.SIGNATURE_FAILED,
                            DocumentSignatureRequestStatus.REQUEST_FAILED
                    ),
                    REQUEST_EXPIRED, List.of(DocumentSignatureRequestStatus.EXPIRED),
                    REQUEST_CANCELED, List.of(DocumentSignatureRequestStatus.CANCELED)
            );

    private static final Map<DocumentSignatureRequestStatus, DocumentSignatureStatus> requestStatusToStatusMap =
            statusToRequestStatusMap.entrySet().stream()
                    .flatMap(entry -> {
                        var status = entry.getKey();
                        var requestStatuses = entry.getValue();
                        return requestStatuses.stream()
                                .map(it -> Pair.of(it, status));
                    })
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));


    public List<DocumentSignatureRequestStatus> requestStatuses() {
        var result = statusToRequestStatusMap.get(this);
        if (result == null) {
            throw new NotImplementedException("Unexpected request status status");
        }
        return result;
    }

    public static DocumentSignatureStatus fromRequestStatus(DocumentSignatureRequestStatus status) {
        var result = requestStatusToStatusMap.get(status);
        if (result == null) {
            throw new IllegalArgumentException("Unexpected request status status");
        }
        return result;
    }
}

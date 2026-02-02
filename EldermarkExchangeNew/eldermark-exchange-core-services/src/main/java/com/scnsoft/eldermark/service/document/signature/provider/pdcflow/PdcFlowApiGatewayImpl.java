package com.scnsoft.eldermark.service.document.signature.provider.pdcflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.service.document.signature.provider.pdcflow.api.DocumentApiDto;
import com.scnsoft.eldermark.service.document.signature.provider.pdcflow.api.OverlayApiDto;
import com.scnsoft.eldermark.service.document.signature.provider.pdcflow.api.SignatureApiDto;
import com.scnsoft.eldermark.service.document.signature.provider.pdcflow.api.TransactionReportApiDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;

@Service
public class PdcFlowApiGatewayImpl implements PdcFlowApiGateway {
    private static final Logger logger = LoggerFactory.getLogger(PdcFlowApiGatewayImpl.class);

    private final Map<Integer, Function<Throwable, ? extends PdcFlowApiException>> codeToExceptionMap =
            Map.of(10101, SignatureAlreadyCanceledPdcFlowApiException::new);

    @Value("${pdcflow.api.host}")
    private String host;

    @Value("${pdcflow.api.endpoint.signatures}")
    private String signaturesEndpoint;

    @Value("${pdcflow.api.endpoint.overlays}")
    private String overlaysEndpoint;

    @Value("${pdcflow.api.endpoint.documents}")
    private String documentsEndpoint;

    @Value("${pdcflow.api.endpoint.transactionReports}")
    private String transactionReportEndpoint;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    public PdcFlowApiGatewayImpl(@Qualifier("jsonRestTemplateBuilder") RestTemplateBuilder restTemplateBuilder,
                                 ObjectMapper objectMapper,
                                 @Value("${pdcflow.api.username}") String userName,
                                 @Value("${pdcflow.api.password}") String password) {
        this.restTemplate = restTemplateBuilder
                .errorHandler(new DefaultResponseErrorHandler())
                .build();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(userName, password));
        this.objectMapper = objectMapper;
    }

    @Override
    public DocumentApiDto postDocument(DocumentApiDto documentApiDto) {
        logger.info("Entered PdcFlowApiGateway postDocument");
        if (StringUtils.isEmpty(documentApiDto.getDocumentName())) {
            throw new PdcFlowApiException("Missing document name");
        }
        if (documentApiDto.getDocumentBase64String() == null) {
            throw new PdcFlowApiException("Missing document content bytes");
        }

        return withTranslatedException(() -> {
            var document = sendPostDocument(documentApiDto);
            logger.info("Uploaded documentId [{}]", document.getDocumentId());
            return document;
        });
    }

    private DocumentApiDto sendPostDocument(DocumentApiDto documentApiDto) {
        var response = restTemplate.postForEntity(host + documentsEndpoint, documentApiDto, DocumentApiDto.class);
        logger.info("PDCFlow uploadDocument api response status: {}", response.getStatusCode());
        if (response.getStatusCode().is2xxSuccessful()) {
            return Objects.requireNonNull(response.getBody());
        } else {
            throw new PdcFlowApiException("PDCFlow uploadDocument api response status: " + response.getStatusCodeValue());
        }
    }

    @Override
    public OverlayApiDto postOverlay(OverlayApiDto overlayApiDto) {
        logger.info("Entered PdcFlowApiGateway postOverlay");
        if (CollectionUtils.isEmpty(overlayApiDto.getOverlayBoxDefinitionList())) {
            throw new PdcFlowApiException("Missing overlay box definitions");
        }

        return withTranslatedException(() -> {
            var createdOverlay = sendPostOverlay(overlayApiDto);
            logger.info("Created overlayId [{}]", createdOverlay.getOverlayId());
            return createdOverlay;
        });
    }

    private OverlayApiDto sendPostOverlay(OverlayApiDto overlayApiDto) {
        var response = restTemplate.postForEntity(host + overlaysEndpoint, overlayApiDto, OverlayApiDto.class);
        logger.info("PDCFlow createOverlay api response status: {}", response.getStatusCode());
        if (response.getStatusCode().is2xxSuccessful()) {
            return Objects.requireNonNull(response.getBody());
        } else {
            throw new PdcFlowApiException("PDCFlow createOverlay api response status: " + response.getStatusCodeValue());
        }
    }

    @Override
    public SignatureApiDto postSignature(SignatureApiDto signatureApiDto) {
        logger.info("Entered PdcFlowApiGateway postSignature");
        if (signatureApiDto.getDocument() == null || signatureApiDto.getDocument().getDocumentId() == null) {
            throw new PdcFlowApiException("Missing document");
        }

        return withTranslatedException(() -> {
            var response = sendPostSignature(signatureApiDto);
            logger.info("Uploaded signature requestId [{}]", response.getSignatureId());
            return response;
        });
    }

    private SignatureApiDto sendPostSignature(SignatureApiDto signatureApiDto) {
        var response = restTemplate.postForEntity(host + signaturesEndpoint, signatureApiDto, SignatureApiDto.class);
        logger.info("PDCFlow postSignature api response status: {}", response.getStatusCode());
        if (response.getStatusCode().is2xxSuccessful()) {
            return Objects.requireNonNull(response.getBody());
        } else {
            throw new PdcFlowApiException("PDCFlow postSignature api response status: " + response.getStatusCodeValue());
        }
    }

    @Override
    public void putSignature(SignatureApiDto signatureApiDto) {
        logger.info("Entered PdcFlowApiGateway putSignature");
        if (signatureApiDto.getSignatureId() == null) {
            throw new PdcFlowApiException("Signature id is missing");
        }

        if (StringUtils.isEmpty(signatureApiDto.getModificationCode())) {
            throw new PdcFlowApiException("Modification code is not set");
        }

        if (StringUtils.isEmpty(signatureApiDto.getUsername())) {
            throw new PdcFlowApiException("Username is not set");
        }

        withTranslatedException(() -> {
            sendPutSignature(signatureApiDto);
            logger.info("Updated signature requestId [{}]", signatureApiDto.getSignatureId());
            return null;
        });
    }

    private void sendPutSignature(SignatureApiDto signatureApiDto) {
        var response = restTemplate.exchange(
                host + signaturesEndpoint + "/" + signatureApiDto.getSignatureId(),
                HttpMethod.PUT,
                new HttpEntity<>(signatureApiDto),
                Void.class);
        logger.info("PDCFlow putSignature api response status: {}", response.getStatusCodeValue());
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new PdcFlowApiException("PDCFlow putSignature api response status: " + response.getStatusCodeValue());
        }
    }

    @Override
    public SignatureApiDto getSignature(BigInteger signatureId) {
        logger.info("Entered PdcFlowApiGateway getSignature");
        return withTranslatedException(() -> sendGetSignatureRequest(signatureId));
    }

    private SignatureApiDto sendGetSignatureRequest(BigInteger pdcflowSignatureId) {
        var response = restTemplate.getForEntity(
                host + signaturesEndpoint + "/" + pdcflowSignatureId.toString(),
                SignatureApiDto.class
        );
        logger.info("PDCFlow getSignature api response status: {}", response.getStatusCode());
        if (response.getStatusCode().is2xxSuccessful()) {
            return Objects.requireNonNull(response.getBody());
        } else {
            throw new PdcFlowApiException("PDCFlow getSignature api response status: " + response.getStatusCodeValue());
        }
    }

    @Override
    public TransactionReportApiDto getTransactionReport(BigInteger signatureId) {
        logger.info("Entered PdcFlowApiGateway getTransactionReport");
        return withTranslatedException(() -> sendGetTransactionReport(signatureId));
    }

    private TransactionReportApiDto sendGetTransactionReport(BigInteger signatureId) {
        var response = restTemplate.getForEntity(
                host + transactionReportEndpoint + "/" + signatureId.toString(),
                TransactionReportApiDto.class
        );
        logger.info("PDCFlow getTransactionReport api response status: {}", response.getStatusCode());
        if (response.getStatusCode().is2xxSuccessful()) {
            return Objects.requireNonNull(response.getBody());
        } else {
            throw new PdcFlowApiException("PDCFlow getTransactionReport api response status: " + response.getStatusCodeValue());
        }
    }

    private <T> T withTranslatedException(Callable<T> callable) {
        try {
            return callable.call();
        } catch (PdcFlowApiException e) {
            throw e;
        } catch (HttpClientErrorException e) {
                Optional.of(e.getResponseBodyAsByteArray())
                        .map(it -> {
                            try {
                                return objectMapper.readTree(it);
                            } catch (IOException ex) {
                                return null;
                            }
                        })
                        .map(it -> it.get("requestErrorList"))
                        .map(it -> it.get(0))
                        .map(it -> it.get("errorCode"))
                        .map(JsonNode::asInt)
                        .map(codeToExceptionMap::get)
                        .ifPresent(it -> {
                            throw it.apply(e);
                        });
            throw new PdcFlowApiException(e);
        } catch (Exception e) {
            throw new PdcFlowApiException(e);
        }
    }
}

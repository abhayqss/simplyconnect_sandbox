package com.scnsoft.eldermark.service.document.signature.provider;

import com.itextpdf.text.BaseColor;
import com.scnsoft.eldermark.beans.projection.DocumentSignatureFieldPdcFlowTypeAware;
import com.scnsoft.eldermark.dto.RectangleDefinition;
import com.scnsoft.eldermark.dto.singature.SignatureStatus;
import com.scnsoft.eldermark.entity.signature.*;
import com.scnsoft.eldermark.service.PdfService;
import com.scnsoft.eldermark.service.UrlShortenerService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureHistoryService;
import com.scnsoft.eldermark.service.document.signature.provider.pdcflow.PdcFlowApiGateway;
import com.scnsoft.eldermark.service.document.signature.provider.pdcflow.PdcFlowJwtTokenService;
import com.scnsoft.eldermark.service.document.signature.provider.pdcflow.SignatureAlreadyCanceledPdcFlowApiException;
import com.scnsoft.eldermark.service.document.signature.provider.pdcflow.api.*;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PdcFlowDocumentSignatureProvider implements DocumentSignatureProvider {
    private static final Logger logger = LoggerFactory.getLogger(PdcFlowDocumentSignatureProvider.class);
    private static final double PDCFLOW_DPI = 240.0;
    private static final int PDF_DPI = 72;
    private static final double POINT_TO_PIXEL_RATIO = PDCFLOW_DPI / PDF_DPI;
    private static final int MAX_FONT_SIZE = 16;
    private static final String MODIFICATION_CODE_CLOSE = "CLOSE";

    @Autowired
    private PdcFlowApiGateway pdcFlowApiGateway;

    @Autowired
    private UrlShortenerService urlShortenerService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private PdcFlowJwtTokenService jwtTokenService;

    @Autowired
    private DocumentSignatureHistoryService documentSignatureHistoryService;

    @Value("${pdcflow.api.redirectUrl.template}")
    private String redirectUrlTemplate;

    @Value("${pdcflow.api.postback}")
    private String postbackUrl;

    @Value("${spring.profiles.active}")
    private String profile;

    public static final BaseColor color = new BaseColor(255, 0, 0, 100);
    public static final List<RectangleDefinition> WATERMARKS_LOCATIONS = List.of(
            //lower rectangle to remove REQUEST ID and PAGE on each page
            new RectangleDefinition(0, null, 0, null, 150, 23,
                    page -> true,
                    BaseColor.WHITE),
            //upper rectangle to remove document id starting from page 3
            new RectangleDefinition(0, null, null, -20, 150, 20,
                    page -> page >= 3,
                    BaseColor.WHITE)
    );

    @Override
    public void prepareSubmittedFieldFromTemplate(DocumentSignatureFieldPdcFlowTypeAware templateField,
                                                  DocumentSignatureRequestSubmittedField submittedField) {
        var templatePdcType = templateField.getPdcFlowType();
        submittedField.setPdcflowOverlayType(resolvePdcFlowOverlayType(templatePdcType).getId());
    }

    private PdcFlowOverlayBoxType resolvePdcFlowOverlayType(TemplateFieldPdcFlowType type) {
        switch (type) {
            case TEXT:
                return PdcFlowOverlayBoxType.TEXT_BOX;
            case CHECKBOX:
                return PdcFlowOverlayBoxType.CHECKBOX;
            case SIGNATURE:
                return PdcFlowOverlayBoxType.SMALLER_SIGNATURE_BOX;
            case SIGNATURE_DATE:
                return PdcFlowOverlayBoxType.UNEDITABLE_CURRENT_DATE;
            default:
                throw new DocumentSignatureProviderException(
                        "Can't resolve PDCFlow overlay boxTypeId from TemplateFieldPdcFlowType." + type.name());
        }
    }

    @Override
    public void sendSignatureRequest(DocumentSignatureRequest signatureRequest,
                                     byte[] documentContent) {
        logger.info("Submitting signature request for id [{}]", signatureRequest.getId());

        try {
            if (!documentSignatureHistoryService.isFirstSignature(signatureRequest)) {
                documentContent = cleanPreviousWatermarks(documentContent);
            }

            var pdcFlowDocumentId = uploadDocument(signatureRequest.getSignatureTemplate().getTitle(), documentContent);

            var pdcFlowOverlayId = createOverlay(
                    pdcFlowDocumentId,
                    documentContent,
                    signatureRequest.getSubmittedFields()
            );

            var signatureDto = createSignatureDto(
                    signatureRequest,
                    pdcFlowDocumentId,
                    pdcFlowOverlayId
            );
            var createdSignature = pdcFlowApiGateway.postSignature(signatureDto);

            signatureRequest.setPdcflowSignatureId(createdSignature.getSignatureId());
            signatureRequest.setPdcflowSignatureUrl(createdSignature.getSignatureUrl());

            logger.info("Successfully submitted signature request for id [{}]", signatureRequest.getId());
        } catch (DocumentSignatureProviderException e) {
            throw e;
        } catch (Exception e) {
            throw new DocumentSignatureProviderException(e);
        }
    }

    private byte[] cleanPreviousWatermarks(byte[] documentContent) throws IOException {
        return pdfService.writeRectangles(documentContent, WATERMARKS_LOCATIONS);
    }

    private BigInteger uploadDocument(String name, byte[] bytes) {
        logger.info("Uploading document...");

        var document = buildDocument(name, bytes);
        return pdcFlowApiGateway.postDocument(document).getDocumentId();
    }

    private DocumentApiDto buildDocument(String name, byte[] bytes) {
        var document = new DocumentApiDto();
        document.setDocumentName(name);
        document.setDocumentBase64String(bytes);
        return document;
    }

    private BigInteger createOverlay(BigInteger originalDocumentId, byte[] document, List<DocumentSignatureRequestSubmittedField> submittedFields) throws IOException {
        logger.info("Creating overlay...");
        if (CollectionUtils.isEmpty(submittedFields)) {
            logger.info("No submitted fields provided - overlayId is null");
            return null;
        }
        var overlayFields = submittedFields.stream()
                .filter(f -> f.getPdcflowOverlayType() != null)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(overlayFields)) {
            logger.info("No fields with overlay type provided - overlayId is null");
            return null;
        }

        var overlay = createOverlayDto(originalDocumentId, document, overlayFields);
        return pdcFlowApiGateway.postOverlay(overlay).getOverlayId();
    }

    private OverlayApiDto createOverlayDto(BigInteger originalDocumentId, byte[] document,
                                           List<DocumentSignatureRequestSubmittedField> overlayFields) throws IOException {
        var overlay = new OverlayApiDto();
        overlay.setOverlayName("overlay-for-" + originalDocumentId.toString());
        overlay.setOriginalDocumentId(originalDocumentId);

        var dimensions = pdfService.pagesDimensions(document);
        overlay.setOverlayBoxDefinitionList(
                overlayFields.stream()
                        .map(field -> mapToOverlayBox(field, dimensions))
                        .collect(Collectors.toList())
        );
        return overlay;
    }

    private OverlayBoxApiDto mapToOverlayBox(DocumentSignatureRequestSubmittedField field,
                                             List<Pair<Float, Float>> allDimensions) {
        var overlayBox = new OverlayBoxApiDto();
        overlayBox.setBoxTypeId(field.getPdcflowOverlayType());
        overlayBox.setDocumentPage(field.getPageNo());

        var dimensions = allDimensions.get(field.getPageNo() - 1);
        var width = dimensions.getFirst();
        var height = dimensions.getSecond();

        overlayBox.setStartXPercent((field.getTopLeftX() * 100) / width);
        overlayBox.setStartYPercent((field.getTopLeftY() * 100) / height);

        if (PdcFlowOverlayBoxType.TEXT_BOX.getId() == field.getPdcflowOverlayType()) {
            var props = new BoxPropertiesApiDto();
            props.setBoxWidth((int) ((field.getBottomRightX() - field.getTopLeftX()) * POINT_TO_PIXEL_RATIO));
            props.setBoxHeight((int) ((field.getBottomRightY() - field.getTopLeftY()) * POINT_TO_PIXEL_RATIO));
            props.setFontSize(Math.min(
                    //this is heuristic formula based on how it looks in PDCFLow
                    field.getBottomRightY() - field.getTopLeftY() - 4,
                    MAX_FONT_SIZE)
            );

            overlayBox.setBoxProperties(props);
        }

        return overlayBox;
    }

    private SignatureApiDto createSignatureDto(DocumentSignatureRequest signatureRequest,
                                               BigInteger pdcFlowDocumentId,
                                               BigInteger pdcFlowOverlayId) {
        var signature = new SignatureApiDto();
        signature.setStandaloneSignatureRequested(false);

        signature.setPostbackUrl(postbackUrl);
        signature.setPostbackAuthHeader(
                jwtTokenService.generateToken(
                        signatureRequest.getId(),
                        signatureRequest.getDateExpires()
                )
        );

        if (signatureRequest.getRequestedFromClient() != null) {
            signature.setFirstName(signatureRequest.getRequestedFromClient().getFirstName());
            signature.setLastName(signatureRequest.getRequestedFromClient().getLastName());
        } else if (signatureRequest.getRequestedFromEmployee() != null) {
            signature.setFirstName(signatureRequest.getRequestedFromEmployee().getFirstName());
            signature.setLastName(signatureRequest.getRequestedFromEmployee().getLastName());
        } else {
            throw new DocumentSignatureProviderException("Missing person who should sign document");
        }

        signature.setTemplateName(
                Optional.ofNullable(signatureRequest.getSignatureTemplate())
                        .map(DocumentSignatureTemplate::getTitle)
                        .orElse(null)
        );

        signature.setTimeoutMinutes((int) Duration.between(Instant.now(), signatureRequest.getDateExpires()).toMinutes());

        if (signatureRequest.getPdcflowPinCode() != null) {
            signature.setVerificationPin(Integer.parseInt(signatureRequest.getPdcflowPinCode()));
        } else {
            signature.setVerificationPin(-1);
        }

        var document = new DocumentApiDto();
        document.setDocumentId(pdcFlowDocumentId);
        document.setOverlayId(pdcFlowOverlayId);
        signature.setDocument(document);

        if (hasSignerAccessToPortal(signatureRequest)) {
            signature.setRedirectLink(
                    urlShortenerService.getShortUrl(String.format(redirectUrlTemplate,
                            signatureRequest.getClient().getId(),
                            signatureRequest.getDocument().getId()
                    ))
            );
        }

        signature.setRequestGeolocation(true);

        var companyOverride = new CompanyOverrideApiDto();
        companyOverride.setCompanyName(signatureRequest.getClient().getCommunity().getName());

        signature.setCompanyOverride(companyOverride);

        return signature;
    }

    private boolean hasSignerAccessToPortal(DocumentSignatureRequest request) {
        return !"local".equalsIgnoreCase(profile) &&
                (request.getNotificationMethod() == SignatureRequestNotificationMethod.SIGN_NOW
                        || request.getRequestedFromClient() == null
                        || CollectionUtils.isNotEmpty(request.getRequestedFromClient().getAssociatedEmployeeIds()));
    }

    @Override
    public void cancelRequest(DocumentSignatureRequest signatureRequest) {
        var signatureApiDto = new SignatureApiDto();
        signatureApiDto.setSignatureId(signatureRequest.getPdcflowSignatureId());
        var canceledBy = signatureRequest.getCanceledByEmployee();
        signatureApiDto.setUsername(canceledBy != null ? canceledBy.getFullName() : "System");
        signatureApiDto.setModificationCode(MODIFICATION_CODE_CLOSE);
        try {
            pdcFlowApiGateway.putSignature(signatureApiDto);
        } catch (SignatureAlreadyCanceledPdcFlowApiException e) {
            throw new SignatureAlreadyCanceledProviderException(e);
        } catch (Exception e) {
            throw new DocumentSignatureProviderException(e);
        }
    }

    @Override
    public SignatureStatus loadSignatureStatus(DocumentSignatureRequest signatureRequest) {
        logger.info("Fetching signature status signature request [{}]", signatureRequest.getId());
        try {
            return mapToStatus(pdcFlowApiGateway.getSignature(signatureRequest.getPdcflowSignatureId()));
        } catch (Exception e) {
            throw new DocumentSignatureProviderException(e);
        }
    }

    private SignatureStatus mapToStatus(SignatureApiDto signatureApiDto) {
        var result = new SignatureStatus();
        result.setStatus(PdcFlowSignatureStatus.valueOf(signatureApiDto.getStatus()));
        result.setErrorCode(signatureApiDto.getErrorCode());
        result.setErrorMessage(signatureApiDto.getErrorMessage());
        result.setStatusDate(signatureApiDto.getStatusDate());
        return result;
    }

    @Override
    public byte[] getSignedDocument(DocumentSignatureRequest signatureRequest) {
        logger.info("Fetching signed document for signature request [{}]", signatureRequest.getId());
        try {
            return pdcFlowApiGateway.getTransactionReport(signatureRequest.getPdcflowSignatureId()).getReportData();
        } catch (Exception e) {
            throw new DocumentSignatureProviderException(e);
        }
    }
}

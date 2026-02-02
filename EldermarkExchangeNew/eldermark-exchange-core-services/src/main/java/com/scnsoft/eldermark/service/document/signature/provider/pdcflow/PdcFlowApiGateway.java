package com.scnsoft.eldermark.service.document.signature.provider.pdcflow;

import com.scnsoft.eldermark.service.document.signature.provider.pdcflow.api.DocumentApiDto;
import com.scnsoft.eldermark.service.document.signature.provider.pdcflow.api.OverlayApiDto;
import com.scnsoft.eldermark.service.document.signature.provider.pdcflow.api.SignatureApiDto;
import com.scnsoft.eldermark.service.document.signature.provider.pdcflow.api.TransactionReportApiDto;

import java.math.BigInteger;

public interface PdcFlowApiGateway {

    DocumentApiDto postDocument(DocumentApiDto documentApiDto);

    OverlayApiDto postOverlay(OverlayApiDto overlayApiDto);

    SignatureApiDto postSignature(SignatureApiDto signatureDto);

    void putSignature(SignatureApiDto signatureApiDto);

    SignatureApiDto getSignature(BigInteger signatureId);

    TransactionReportApiDto getTransactionReport(BigInteger signatureId);
}

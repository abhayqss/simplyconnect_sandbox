package com.scnsoft.eldermark.service.document.signature.provider.pdcflow;

import java.math.BigInteger;
import java.time.Instant;

public interface PdcFlowJwtTokenService {
    String generateToken(Long signatureRequestId, Instant expiresAt);

    boolean validateToken(String token, Long actualDocumentSignatureRequestId);
}

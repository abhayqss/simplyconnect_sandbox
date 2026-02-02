package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.mobile.dto.signature.DocumentSignatureHistoryDto;
import com.scnsoft.eldermark.mobile.dto.signature.DocumentSignatureRequestDto;
import com.scnsoft.eldermark.mobile.dto.signature.DocumentSignatureRequestRenewDto;
import com.scnsoft.eldermark.mobile.dto.signature.DocumentSignatureResendPinResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocumentSignatureRequestFacade {

    DocumentSignatureRequestDto getById(Long requestId);

    Page<DocumentSignatureHistoryDto> findHistoryByDocumentId(Long documentId, Pageable pageable, Integer timezoneOffset);

    Long renewRequest(DocumentSignatureRequestRenewDto dto);

    void cancelRequest(Long requestId);

    DocumentSignatureResendPinResponseDto resendPin(Long requestId);

    boolean canAdd(Long clientId);
}

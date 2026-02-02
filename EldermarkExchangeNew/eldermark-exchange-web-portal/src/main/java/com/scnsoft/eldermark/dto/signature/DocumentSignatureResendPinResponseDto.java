package com.scnsoft.eldermark.dto.signature;

public class DocumentSignatureResendPinResponseDto {

    private String receiverPhone;

    public DocumentSignatureResendPinResponseDto(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }
}

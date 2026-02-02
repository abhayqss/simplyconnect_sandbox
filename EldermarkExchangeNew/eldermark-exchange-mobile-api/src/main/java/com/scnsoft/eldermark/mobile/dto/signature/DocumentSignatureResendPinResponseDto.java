package com.scnsoft.eldermark.mobile.dto.signature;

public class DocumentSignatureResendPinResponseDto {

    private String receiverPhone;

    private Long canResendPinAt;

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public Long getCanResendPinAt() {
        return canResendPinAt;
    }

    public void setCanResendPinAt(Long canResendPinAt) {
        this.canResendPinAt = canResendPinAt;
    }
}

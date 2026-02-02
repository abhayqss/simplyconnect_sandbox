package com.scnsoft.eldermark.dto.document;

public class DocumentSignatureInfoDto {

    private Long requestId;
    private String statusName;
    private String statusTitle;
    private String pdcFlowPinCode;
    private String pdcFlowLink;
    private boolean canRequest;
    private boolean canSign;
    private boolean canResendPdcFlowPinCode;
    private boolean canCancelRequest;
    private boolean hasAvailableAreas;
    private boolean hasAreas;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public String getPdcFlowPinCode() {
        return pdcFlowPinCode;
    }

    public void setPdcFlowPinCode(String pdcFlowPinCode) {
        this.pdcFlowPinCode = pdcFlowPinCode;
    }

    public String getPdcFlowLink() {
        return pdcFlowLink;
    }

    public void setPdcFlowLink(String pdcFlowLink) {
        this.pdcFlowLink = pdcFlowLink;
    }

    public boolean getCanRequest() {
        return canRequest;
    }

    public void setCanRequest(boolean canRequest) {
        this.canRequest = canRequest;
    }

    public boolean getCanSign() {
        return canSign;
    }

    public void setCanSign(boolean canSign) {
        this.canSign = canSign;
    }

    public boolean getCanResendPdcFlowPinCode() {
        return canResendPdcFlowPinCode;
    }

    public void setCanResendPdcFlowPinCode(boolean canResendPdcFlowPinCode) {
        this.canResendPdcFlowPinCode = canResendPdcFlowPinCode;
    }

    public boolean getCanCancelRequest() {
        return canCancelRequest;
    }

    public void setCanCancelRequest(boolean canCancelRequest) {
        this.canCancelRequest = canCancelRequest;
    }

    public boolean getHasAvailableAreas() {
        return hasAvailableAreas;
    }

    public void setHasAvailableAreas(boolean hasAvailableAreas) {
        this.hasAvailableAreas = hasAvailableAreas;
    }

    public boolean getHasAreas() {
        return hasAreas;
    }

    public void setHasAreas(boolean hasAreas) {
        this.hasAreas = hasAreas;
    }
}
